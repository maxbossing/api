package ng.bossi.api.http.routes.authenticated

import io.ktor.server.routing.*

fun Route.initAuthenticatedRoutes() {
  route("/application") {
    authenticatedApplicationRoutes()
    authenticatedFeatureFlagRoutes()
    authenticatedSingleLicenseRoutes()
  }
}