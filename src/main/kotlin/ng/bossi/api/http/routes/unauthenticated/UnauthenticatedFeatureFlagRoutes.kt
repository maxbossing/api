package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.FeatureFlag
import ng.bossi.api.signing.signature
import ng.bossi.api.utils.RateLimits
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.rateLimit

fun Route.unauthenticatedFeatureFlagRoutes() {
  rateLimit(RateLimits.FEATUREFLAG) {
    get("/{applicationName}/featureFlags/{featureFlag}") {
      call.applicationCall { applicationId, applicationName, _ ->
        val knowOnce = call.request.headers["X-Know-Once"]
        if (knowOnce == null) {
          call.respond(HttpStatusCode.BadRequest, "Missing X-Know-Once Header!")
          return@applicationCall
        }
        val featureFlagName = call.parameters["featureFlag"]

        if (featureFlagName == null) {
          call.respond(HttpStatusCode.BadRequest, "Invalid FeatureFlag!")
          return@applicationCall
        }

        val featureFlag = DatabaseController.featureFlagService.read(featureFlagName, applicationId)

        if (featureFlag == null) {
          call.respond(HttpStatusCode.NotFound, "FeatureFlag Not Found!")
          return@applicationCall
        }

        call.response.headers.append("Signature", featureFlag.second.signature(applicationId, FeatureFlag.serializer(), knowOnce)!!)
        call.respond(featureFlag.second)
      }
    }
  }
}