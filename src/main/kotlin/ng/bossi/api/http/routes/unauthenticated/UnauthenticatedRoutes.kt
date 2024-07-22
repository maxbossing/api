package ng.bossi.api.http.routes.unauthenticated

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.initUnauthenticatedRoutes() {
  get("/helloworld") { call.respondText("Hello World!") }

  unauthenticatedVersionRoutes()
  unauthenticatedFeatureFlagRoutes()
  unauthenticatedSingleLicenseRoutes()
  unauthenticatedResourceRoutes()
}
