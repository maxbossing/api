package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.Version
import ng.bossi.api.signing.signature
import ng.bossi.api.utils.applicationCall

fun Route.unauthenticatedVersionRoutes() {
  get("/{applicationName}/versions") {
    call.applicationCall { applicationId, applicationName, application ->
      val knowOnce = call.request.headers["Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing Know-Once Header!")
        return@applicationCall
      }
      val version = DatabaseController.applicationService.getNewestVersion(applicationId)

      if (version == null) {
        call.respond(HttpStatusCode.NotFound, "No Current Version found!")
        return@applicationCall
      }

      call.response.headers.append("Signature", version.signature(applicationId, Version.serializer(), knowOnce)!!)
      call.respond<Version>(version)
    }
  }

  get("/{applicationName}/versions/{versionName}") {
    call.applicationCall { applicationId, applicationName, application ->

      val knowOnce = call.request.headers["Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing Know-Once Header!")
        return@applicationCall
      }
      val versionName = call.parameters["versionName"]

      if (versionName == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid Version Name!")
        return@applicationCall
      }

      val version = DatabaseController.versionService.readByName(versionName, applicationId)

      if (version == null) {
        call.respond(HttpStatusCode.NotFound, "No Version found!")
        return@applicationCall
      }

      call.response.headers.append("Signature", version.signature(applicationId, Version.serializer(), knowOnce)!!)
      call.respond<Version>(version)
    }
  }
}
