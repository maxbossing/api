package ng.bossi.api.http.routes.unauthenticated

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.initUnauthenticatedRoutes() {
  get("/helloworld") { call.respondText("Hello World!") }
  route("/application") {
    unauthenticatedVersionRoutes()
    unauthenticatedFeatureFlagRoutes()
    unauthenticatedSingleLicenseRoutes()
  }
  route("/resources") {
    unauthenticatedResourceRoutes()
  }
}
