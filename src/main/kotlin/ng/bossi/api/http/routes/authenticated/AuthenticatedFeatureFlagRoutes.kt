package ng.bossi.api.http.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.FeatureFlag
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.boolParameter
import ng.bossi.api.utils.parameter


@Serializable
data class FeatureFlagResponse(
  val id: Long,
  val name: String,
  val application: Long,
  val enabled: Boolean,
)

fun Route.authenticatedFeatureFlagRoutes() {
  /* WARN: Uniqueness of featureflag name HAS to be enforced manually in combination with application */
  put("/application/{applicationName}/featureFlags/create") {
    call.applicationCall { applicationId, application ->
      val name = call.parameter("name") ?: return@put
      val enabled = call.boolParameter("enabled") ?: return@put

      if (DatabaseController.featureFlagService.nameToId(name) != null) {
        call.respond(HttpStatusCode.Conflict, "Feature flag already exists")
        return@put
      }

      val id = DatabaseController.featureFlagService.create(FeatureFlag(name, applicationId, enabled))

      call.respond<FeatureFlagResponse>(FeatureFlagResponse(id, name, applicationId, enabled))
    }
  }

  patch("/application/{applicationName}/featureFlags/{flagName}/update") {
    call.applicationCall { applicationId, application ->
      val flagName = call.parameters["flagName"]
      if (flagName == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid FeatureFlag!")
        return@applicationCall
      }

      val flag = DatabaseController.featureFlagService.readByName(flagName)
      if (flag == null) {
        call.respond(HttpStatusCode.NotFound, "FeatureFlag does not exist!")
        return@applicationCall
      }

      val enabled = call.boolParameter("enabled") ?: return@applicationCall

      if (!DatabaseController.featureFlagService.update(
        flag.first,
        FeatureFlag(
          name = flag.second.name,
          application = flag.second.application,
          enabled = enabled
        )
      )) {
        call.respond(HttpStatusCode.InternalServerError, "FeatureFlag update failed!")
        return@applicationCall
      }
      call.respond(HttpStatusCode.OK, FeatureFlagResponse(flag.first, flag.second.name, flag.second.application, enabled))
    }
  }

  delete("/application/{applicationName}/featureFlags/{flagName}/delete") {
    call.applicationCall { applicationId, application ->
      val flagName = call.parameters["flagName"]
      if (flagName == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid FeatureFlag!")
        return@applicationCall
      }

      val flag = DatabaseController.featureFlagService.nameToId(flagName)
      if (flag == null) {
        call.respond(HttpStatusCode.NotFound, "FeatureFlag does not exist!")
        return@applicationCall
      }

      val success = DatabaseController.featureFlagService.delete(flag)
      if (!success) {
        call.respond(HttpStatusCode.InternalServerError, "FeatureFlag delete failed!")
        return@applicationCall
      }

      call.respond(HttpStatusCode.OK, "FeatureFlag deleted successfully!")
    }
  }
}