package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.utils.applicationCall

fun Route.unauthenticatedFeatureFlagRoutes() {
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
}