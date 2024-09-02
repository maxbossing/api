package ng.bossi.api.http.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.auth.BearerPermission
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.FeatureFlag
import ng.bossi.api.model.FeatureFlagState
import ng.bossi.api.signing.signature
import ng.bossi.api.utils.applicationCall
import ng.bossi.api.utils.boolParameter
import ng.bossi.api.utils.enumParameter
import ng.bossi.api.utils.parameter


fun Route.authenticatedFeatureFlagRoutes() {
  /* WARN: Uniqueness of featureflag name HAS to be enforced manually in combination with application */
  put("/{applicationName}/featureFlags/create") {
    if (!call.principal<BearerPermission>()!!.featureFlag.create) {
      call.respond(HttpStatusCode.Unauthorized)
      return@put
    }
    call.applicationCall { applicationId, _, _ ->
      val knowOnce = call.request.headers["X-Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing X-Know-Once Header!")
        return@applicationCall
      }
      val name = call.parameter("name") ?: return@put
      val state = call.enumParameter<FeatureFlagState>("state") ?: return@put

      if (DatabaseController.featureFlagService.read(application = applicationId, name = name) != null) {
        call.respond(HttpStatusCode.Conflict, "Feature flag already exists")
        return@put
      }

      val flag = FeatureFlag(name, applicationId, state)

      DatabaseController.featureFlagService.create(flag)

      call.response.headers.append("Signature", flag.signature(applicationId, FeatureFlag.serializer(), knowOnce)!!)
      call.respond<FeatureFlag>(flag)
    }
  }

  patch("/{applicationName}/featureFlags/{flagName}/update") {
    if (!call.principal<BearerPermission>()!!.featureFlag.updateState) {
      call.respond(HttpStatusCode.Unauthorized)
      return@patch
    }
    call.applicationCall { applicationId, _, _ ->
      val knowOnce = call.request.headers["X-Know-Once"]
      if (knowOnce == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing X-Know-Once Header!")
        return@applicationCall
      }

      val flagName = call.parameters["flagName"]!!
      if (flagName.isBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Feature flag name cannot be empty")
        return@applicationCall
      }

      if (DatabaseController.featureFlagService.read(flagName, applicationId) == null) {
        call.respond(HttpStatusCode.NotFound, "FeatureFlag does not exist!")
        return@applicationCall
      }

      val state = call.enumParameter<FeatureFlagState>("state") ?: return@applicationCall

      val success = DatabaseController.featureFlagService.update(flagName, applicationId, state)

      if (!success) {
        call.respond(HttpStatusCode.InternalServerError, "Failed to update FeatureFlag!")
        return@applicationCall
      }

      val flag = FeatureFlag(flagName, applicationId, state)

      call.response.headers.append("Signature", flag.signature(applicationId, FeatureFlag.serializer(), knowOnce)!!)
      call.respond<FeatureFlag>(
        HttpStatusCode.OK,
        FeatureFlag(flagName, applicationId, state),
      )
    }
  }

  delete("/{applicationName}/featureFlags/{flagName}") {
    if (!call.principal<BearerPermission>()!!.featureFlag.delete) {
      call.respond(HttpStatusCode.Unauthorized)
      return@delete
    }
    call.applicationCall { applicationId, _, _ ->
      val flagName = call.parameters["flagName"]
      if (flagName == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid FeatureFlag!")
        return@applicationCall
      }

      val flag = DatabaseController.featureFlagService.read(flagName, applicationId)
      if (flag == null) {
        call.respond(HttpStatusCode.NotFound, "FeatureFlag does not exist!")
        return@applicationCall
      }

      val success = DatabaseController.featureFlagService.deleteById(flag.first)
      if (!success) {
        call.respond(HttpStatusCode.InternalServerError, "FeatureFlag delete failed!")
        return@applicationCall
      }

      call.respond(HttpStatusCode.OK, "FeatureFlag deleted successfully!")
    }
  }
}