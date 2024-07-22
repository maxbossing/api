package ng.bossi.api.http.routes.authenticated

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.initAuthenticatedRoutes() {
  authenticatedApplicationRoutes()
  authenticatedFeatureFlagRoutes()
}