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
  get("application/{id}/versions") {
    val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
    val version = DatabaseController.applicationService.getCurrentVersion(id)
    if (version == null) {
      call.respond(HttpStatusCode.NotFound, "No Current Version found!")
    } else {
      call.respond<SignedVersionResponse>(version.sign(id)!!)
    }
  }
}