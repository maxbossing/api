package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.SignedVersionResponse
import ng.bossi.api.utils.applicationCall

fun Route.unauthenticatedVersionRoutes() {
  get("application/{applicationId}/versions") {
    call.applicationCall { applicationId, application ->
      val version = DatabaseController.applicationService.getCurrentVersion(applicationId)

      if (version == null) {
        call.respond(HttpStatusCode.NotFound, "No Current Version found!")
        return@applicationCall
      }

      call.respond<SignedVersionResponse>(version.sign(applicationId)!!)
    }
  }

  get("application/{applicationID}/versions/{versionId}") {
    call.applicationCall { applicationId, application ->

      val versionId = call.parameters["versionId"]?.toLong()

      if (versionId == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid VersionId!")
        return@applicationCall
      }

      val version = DatabaseController.versionService.read(versionId)

      if (version == null) {
        call.respond(HttpStatusCode.NotFound, "No Version found!")
        return@applicationCall
      }

      call.respond<SignedVersionResponse>(version.sign(applicationId)!!)
    }
  }
}
