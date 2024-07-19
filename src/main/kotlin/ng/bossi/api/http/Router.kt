package ng.bossi.api.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.SignedVersionResponse

fun Routing.initRoutes() {
  get("/helloworld") {
    call.respondText("Hello World!")
  }

  get("application/{applicationId}/versions") {
    val applicationId = call.parameters["applicationId"]?.toLong()

    if (applicationId == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid ApplicationId")
      return@get
    }

    val version = DatabaseController.applicationService.getCurrentVersion(applicationId)

    if (version == null) {
      call.respond(HttpStatusCode.NotFound, "No Current Version found!")
      return@get
    }

    call.respond<SignedVersionResponse>(version.sign(applicationId)!!)
  }

  get("application/{applicationID}/versions/{versionId}") {
    val applicationId = call.parameters["applicationId"]?.toLong()

    if (applicationId == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid ApplicationId!")
      return@get
    }

    val versionId = call.parameters["versionId"]?.toLong()

    if (versionId == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid VersionId!")
      return@get
    }

    val version = DatabaseController.versionService.read(versionId)

    if (version == null) {
      call.respond(HttpStatusCode.NotFound, "No Version found!")
      return@get
    }

    call.respond<SignedVersionResponse>(version.sign(applicationId)!!)
  }

  get("application/{applicationId}/featureFlags/{featureFlag}") {
    val applicationId = call.parameters["applicationId"]?.toLong()

    if (applicationId == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid ApplicationId!")
      return@get
    }

    val featureFlagp = call.parameters["featureFlag"]

    if (featureFlagp == null) {
      call.respond(HttpStatusCode.BadRequest, "Invalid FeatureFlag!")
      return@get
    }

    val featureFlagId = DatabaseController.featureFlagService.nameToId(featureFlagp)

    if (featureFlagId == null) {
      call.respond(HttpStatusCode.NotFound, "Feature flag does not exist!")
      return@get
    }

    val featureFlag = DatabaseController.featureFlagService.read(featureFlagId)!!

    call.respond(featureFlag.sign(applicationId)!!)
  }
}