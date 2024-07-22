package ng.bossi.api.http.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.auth.BearerPermission
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.SingleLicense
import ng.bossi.api.model.SingleLicenseStatus
import ng.bossi.api.signing.signature
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.enumParameter
import ng.bossi.api.utils.longParameter
import ng.bossi.api.utils.toUUIDOrNull


fun Route.authenticatedSingleLicenseRoutes() {
  post("/{applicationName}/license/single/create") {
    if (!call.principal<BearerPermission>()!!.singleLicense.create) {
      call.respond(HttpStatusCode.Unauthorized)
      return@post
    }
    call.applicationCall { applicationId, applicationName, application ->
      val knowOnce = call.request.headers["Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing Know-Once Header!")
        return@applicationCall
      }
      val status = call.enumParameter<SingleLicenseStatus>("status")
      if (status == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid status")
        return@applicationCall
      }

      val id = DatabaseController.singleLicenseService.create(applicationId, status)
      val license = DatabaseController.singleLicenseService.readById(id.first)!!

      call.response.headers.append("Signature", license.signature(applicationId, SingleLicense.serializer(), knowOnce)!!)
      call.respond<SingleLicense>(HttpStatusCode.OK, license)
    }
  }

  patch("/{applicationName}/license/single/{licenseId}/update") {
    if (!call.principal<BearerPermission>()!!.singleLicense.updateStatus) {
      call.respond(HttpStatusCode.Unauthorized)
      return@patch
    }
    call.applicationCall { applicationId, applicationName, application ->
      val knowOnce = call.request.headers["Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing Know-Once Header!")
        return@applicationCall
      }
      val licenseId = call.longParameter("licenseId") ?: return@patch
      val status = call.enumParameter<SingleLicenseStatus>("status") ?: return@patch

      val license = DatabaseController.singleLicenseService.readById(licenseId)

      if (license == null) {
        call.respond(HttpStatusCode.NotFound, "License not found")
        return@applicationCall
      }

      val newLicense = license.copy(status = status)
      if (!DatabaseController.singleLicenseService.updateById(licenseId, newLicense)) {
        call.respond(HttpStatusCode.InternalServerError, "License update failed!")
        return@applicationCall
      }

      call.response.headers.append("Signature", newLicense.signature(applicationId, SingleLicense.serializer(), knowOnce)!!)
      call.respond<SingleLicense>(HttpStatusCode.OK, newLicense)
    }
  }

  delete("/{applicationName}/license/single/{licenseKey}") {
    if (!call.principal<BearerPermission>()!!.singleLicense.delete) {
      call.respond(HttpStatusCode.Unauthorized)
      return@delete
    }
    call.applicationCall { applicationId, applicationName, application ->
      val licenseKey = call.parameters["licenseKey"]!!.toUUIDOrNull()
      if (licenseKey == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid License Key!")
        return@applicationCall
      }

      val success = DatabaseController.singleLicenseService.deleteByKey(licenseKey)
      if (!success) {
        call.respond(HttpStatusCode.NotFound, "SingleLicense not found!")
        return@applicationCall
      }

      call.respond(HttpStatusCode.OK, "SingleLicense deleted successfully!")
    }
  }
}