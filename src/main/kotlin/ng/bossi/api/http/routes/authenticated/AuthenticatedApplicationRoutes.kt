package ng.bossi.api.http.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import ng.bossi.api.auth.BearerPermission
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.Application
import ng.bossi.api.signing.KeyGenerator
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.parameter

@Serializable
private data class ApplicationResponse(val name: String, val key: String)


fun Route.authenticatedApplicationRoutes() {
  put("/create") {
    if (!call.principal<BearerPermission>()!!.application.create) {
      call.respond(HttpStatusCode.Unauthorized)
      return@put
    }

    val name = call.parameter("name") ?: return@put

    val exists = DatabaseController.applicationService.readByName(name) != null

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

    call.respond(
      HttpStatusCode.Created,
      ApplicationResponse(
        name = name,
        key = key.public.encoded.encodeBase64()
      )
    )
  }

  patch("/{applicationName}/update/key") {
    if (!call.principal<BearerPermission>()!!.application.updateKey) {
      call.respond(HttpStatusCode.Unauthorized)
      return@patch
    }
    call.applicationCall { applicationId, applicationName, application ->

      val key = KeyGenerator.generateKey()

      if (!DatabaseController
          .applicationService
          .updateById(
            applicationId,
            key.private.encoded
          )
      ) {
        call.respond(HttpStatusCode.InternalServerError, "Application Update failed!")
        return@applicationCall
      }

      call.respond(
        HttpStatusCode.OK,
        ApplicationResponse(
          applicationName,
          key.public.encoded.encodeBase64(),
        )
      )
    }
  }
  delete("/{applicationName}") {
    if (!call.principal<BearerPermission>()!!.application.delete) {
      call.respond(HttpStatusCode.Unauthorized)
      return@delete
    }
    call.applicationCall { applicationId, applicationName, application ->

      val success = DatabaseController.applicationService.deleteById(applicationId)
      if (!success) {
        call.respond(HttpStatusCode.InternalServerError, "Application delete failed!")
        return@applicationCall
      }

      call.respond(HttpStatusCode.OK, "Application deleted successfully!")
    }
  }
}