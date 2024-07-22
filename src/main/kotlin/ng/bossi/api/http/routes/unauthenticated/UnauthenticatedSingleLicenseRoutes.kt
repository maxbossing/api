package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.SingleLicense
import ng.bossi.api.model.Version
import ng.bossi.api.signing.signature
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.toUUIDOrNull

fun Route.unauthenticatedSingleLicenseRoutes() {
  get("/{applicationName}/license/single/{licenseKey}") {
    call.applicationCall { applicationId, applicationName, application ->
      val knowOnce = call.request.headers["Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing Know-Once Header!")
        return@applicationCall
      }
      val licenseKey = call.parameters["licenseKey"]?.toUUIDOrNull()
      if (licenseKey == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid License Key!")
        return@applicationCall
      }

      val license = DatabaseController.singleLicenseService.readByKey(licenseKey)
      if (license == null) {
        call.respond(HttpStatusCode.NotFound, "No License found!")
        return@applicationCall
      }

      call.response.headers.append("Signature", license.signature(applicationId, SingleLicense.serializer(), knowOnce)!!)
      call.respond<SingleLicense>(HttpStatusCode.OK, license)
    }
  }
}