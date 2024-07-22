package ng.bossi.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ng.bossi.api.auth.BearerPermission
import ng.bossi.api.auth.BearerPermissions
import ng.bossi.api.config.ConfigController
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.http.routes.authenticated.initAuthenticatedRoutes
import ng.bossi.api.http.routes.unauthenticated.initUnauthenticatedRoutes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("API")

lateinit var ktorServer: ApplicationEngine

suspend fun main() = coroutineScope {

  logger.info("Starting API...")

  ConfigController
  DatabaseController

  if (DatabaseController.bearerTokenService.count() == 0L) {
    logger.warn("There is no authentication token stored on disk")
    logger.warn("The following token will be created with all Permissions")
    logger.warn("And will be only shown once!")
    logger.warn(DatabaseController.bearerTokenService.create(BearerPermissions.ALL.bit))
  }


  logger.info("Starting KTOR Server...")

  launch {
    ktorServer = embeddedServer(CIO, 8080) {
      install(ContentNegotiation) {
        json()
      }
      install(Authentication) {
        bearer("auth-bearer") {
          authenticate {
            it.token
            val permissions = DatabaseController.bearerTokenService.getPermissions(it.token) ?: return@authenticate null
            BearerPermission.fromLong(permissions)
          }
        }
      }
      install(Routing) {
        route("/v1") {
          initUnauthenticatedRoutes()
          authenticate("auth-bearer") {
            initAuthenticatedRoutes()
          }
        }
      }
    }.start(true)
  }

  logger.info("API Started!")
}