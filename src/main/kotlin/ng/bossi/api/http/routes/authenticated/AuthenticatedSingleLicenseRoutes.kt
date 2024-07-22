package ng.bossi.api.http.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.SingleLicense
import ng.bossi.api.database.model.SingleLicenseStatus
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.enumParameter
import ng.bossi.api.utils.longParameter


@Serializable
data class SingleLicenseResponse(
  val id: Long,
  val status: SingleLicenseStatus,
  val application: Long
)

fun Route.authenticatedSingleLicenseRoutes() {
  post("/application/{applicationId}/license/single/create") {
    call.applicationCall { applicationId, application ->
      val status = call.enumParameter<SingleLicenseStatus>("status")
      if (status == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid status")
        return@applicationCall
      }

      val id = DatabaseController.singleLicenseService.create(
        SingleLicense(
          application = applicationId,
          status = status
        )
      )

      call.respond(
        SingleLicenseResponse(
          id,
          status,
          applicationId
        )
      )
    }
  }

  patch("/application/{applicationId}/license/single/{licenseId}/update") {
    call.applicationCall { applicationId, application ->
      val licenseId = call.longParameter("licenseId") ?: return@patch
      val status = call.enumParameter<SingleLicenseStatus>("status") ?: return@patch

      val license = DatabaseController.singleLicenseService.read(licenseId)

      if (license == null) {
        call.respond(HttpStatusCode.NotFound, "License not found")
        return@applicationCall
      }

      val newLicense = license.copy(status = status)
      if (!DatabaseController.singleLicenseService.update(licenseId, newLicense)) {
        call.respond(HttpStatusCode.InternalServerError, "License update failed!")
        return@applicationCall
      }

      call.respond(HttpStatusCode.OK, newLicense)
    }
  }

  delete("/application/{applicationId}/license/single/{licenseId}/delete") {
    call.applicationCall { applicationId, application ->
      val licenseId = call.longParameter("licenseId") ?: return@delete

      val success = DatabaseController.singleLicenseService.delete(licenseId)
      if (!success) {
        call.respond(HttpStatusCode.InternalServerError, "FeatureFlag delete failed!")
        return@applicationCall
      }

      call.respond(HttpStatusCode.OK, "FeatureFlag deleted successfully!")
    }
  }
}