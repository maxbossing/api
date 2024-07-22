package ng.bossi.api.http.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.Application
import ng.bossi.api.signing.KeyGenerator
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.parameter
import javax.xml.crypto.Data


@Serializable
data class ApplicationResponse(
  val name: String,
  val publicKey: String,
  val applicationId: Long
)

fun Route.authenticatedApplicationRoutes() {
  put("/application/create") {
    val name = call.parameter("name") ?: return@put

    val exists = DatabaseController.applicationService.nameToId(name) != null

    if (exists) {
      call.respond(HttpStatusCode.Conflict, "Application with given name already exists!")
      return@put
    }
    val key = KeyGenerator.generateKey()
    val id = DatabaseController.applicationService.create(
      Application(
        name = name,
        key = key.private.encoded
      )
    )
    call.respond<ApplicationResponse>(
      HttpStatusCode.Created,
      ApplicationResponse(
        name,
        key.public.encoded.encodeBase64(),
        id
      )
    )
  }
  patch("/application/update/{applicationName}/key") {
    call.applicationCall { applicationName: String ->

      val application = DatabaseController.applicationService.getByName(applicationName)
      if (application == null) {
        call.respond(HttpStatusCode.NotFound, "Application not found!")
        return@applicationCall
      }

      val key = KeyGenerator.generateKey()

      if (!DatabaseController
        .applicationService
        .update(
          application.first,
          application.second.copy(name = applicationName, key = key.private.encoded)
        )) {
        call.respond(HttpStatusCode.InternalServerError, "Application Update failed!")
        return@applicationCall
      }

      call.respond(
        HttpStatusCode.OK,
        ApplicationResponse(
          applicationName,
          key.public.encoded.encodeBase64(),
          application.first,
        )
      )
    }
  }
  delete("/application/{applicationName}") {
    call.applicationCall { applicationName ->
      val application = DatabaseController.applicationService.getByName(applicationName)
      if (application == null) {
        call.respond(HttpStatusCode.NotFound, "Application not found!")
        return@applicationCall
      }
      val success = DatabaseController.applicationService.delete(application.first)
      if (!success) {
        call.respond(HttpStatusCode.InternalServerError, "Application delete failed!")
        return@applicationCall
      }
      call.respond(HttpStatusCode.OK, "Application deleted successfully!")
    }
  }
}

suspend inline fun ApplicationCall.applicationCall(
  block: (applicationName: String) -> Unit
) {
  val applicationName = parameters["applicationName"]

  if (applicationName.isNullOrBlank()) {
    respond(HttpStatusCode.BadRequest, "Invalid Application Name!")
    return
  }

  block(applicationName)
}
