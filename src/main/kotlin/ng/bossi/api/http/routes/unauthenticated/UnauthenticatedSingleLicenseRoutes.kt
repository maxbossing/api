package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.utils.applicationCall

fun Route.unauthenticatedSingleLicenseRoutes() {
  get("application/{applicationId}/license/single/{licenseId}") {
    call.applicationCall { applicationId, application ->
      val licenseId = call.parameters["licenseId"]?.toLong()
      if (licenseId == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid License ID!")
        return@applicationCall
      }

      val license = DatabaseController.singleLicenseService.read(licenseId)
      if (license == null) {
        call.respond(HttpStatusCode.NotFound, "No License found!")
        return@applicationCall
      }

      call.respond(license.sign(applicationId)!!)
    }
  }
}