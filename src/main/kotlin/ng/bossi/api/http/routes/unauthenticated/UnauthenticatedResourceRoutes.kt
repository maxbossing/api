package ng.bossi.api.http.routes.unauthenticated

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.utils.RateLimits
import ng.bossi.api.utils.rateLimit
import ng.bossi.api.utils.resourceCall

fun Route.unauthenticatedResourceRoutes() {
  rateLimit(RateLimits.RESOURCES) {
    get("/{resourceId}") {
      call.resourceCall { _, resource ->
        call.respond(resource)
      }
    }

    get("/{resourceId}/download") {
      call.resourceCall { _, resource ->
        call.respondRedirect(resource.url)
      }
    }
  }

}