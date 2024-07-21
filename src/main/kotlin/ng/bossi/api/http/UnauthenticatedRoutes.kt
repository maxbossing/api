package ng.bossi.api.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.Application
import ng.bossi.api.database.model.SignedVersionResponse

fun Routing.initRoutes() {
  get("/helloworld") { call.respondText("Hello World!") }

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

  get("application/{applicationId}/featureFlags/{featureFlag}") {
    call.applicationCall { applicationId, _ ->
      val featureFlagName = call.parameters["featureFlag"]

      if (featureFlagName == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid FeatureFlag!")
        return@applicationCall
      }

      val featureFlagId = DatabaseController.featureFlagService.nameToId(featureFlagName)

      if (featureFlagId == null) {
        call.respond(HttpStatusCode.NotFound, "Feature flag does not exist!")
        return@applicationCall
      }

      val featureFlag = DatabaseController.featureFlagService.read(featureFlagId)!!

      call.respond(featureFlag.sign(applicationId)!!)
    }
  }

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

  get("resources/{resourceId}") {
    val resourceId = call.parameters["resourceId"]?.toLong()
    if (resourceId == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid Resource ID!")
      return@get
    }

    val resource = DatabaseController.resourceService.read(resourceId)
    if (resource == null) {
      call.respond(HttpStatusCode.NotFound, "No Resource found!")
      return@get
    }

    call.respond(resource)
  }

  get ("resources/{resourceId}/download") {
    val resourceId = call.parameters["resourceId"]?.toLong()
    if (resourceId == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid Resource ID!")
      return@get
    }

    val resource = DatabaseController.resourceService.read(resourceId)
    if (resource == null) {
      call.respond(HttpStatusCode.NotFound, "No Resource found!")
      return@get
    }
    call.respondRedirect(resource.url)
  }
}

private suspend fun ApplicationCall.applicationCall(
  block: suspend (applicationId: Long, application: Application) -> Unit
) {
  val applicationId = parameters["applicationId"]?.toLongOrNull()

  if (applicationId == null) {
    respond(HttpStatusCode.BadRequest, "Invalid ApplicationId!")
    return
  }

  val application = DatabaseController.applicationService.read(applicationId)


  if (application == null) {
    respond(HttpStatusCode.NotFound, "Application not found!")
    return
  }

  block(applicationId, application)
}