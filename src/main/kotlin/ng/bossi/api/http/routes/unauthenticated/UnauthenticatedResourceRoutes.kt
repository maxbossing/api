package ng.bossi.api.http.routes.unauthenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ng.bossi.api.database.DatabaseController

fun Route.unauthenticatedResourceRoutes() {
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

  get("resources/{resourceId}/download") {
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