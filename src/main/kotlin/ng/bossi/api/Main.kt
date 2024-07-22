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

  logger.info("Starting KTOR Server...")

  launch {
    ktorServer = embeddedServer(CIO, 8080) {
      install(ContentNegotiation) {
        json()
      }
      install(Authentication) {
        basic("auth-basic") {
          realm = "Access to the '/auth' path"
          validate { credentials ->
            if (credentials.name == "admin" && credentials.password == "admin")
              UserIdPrincipal(credentials.name)
            else null
          }
        }
      }
      install(Routing) {
        initUnauthenticatedRoutes()
        authenticate("auth-basic") {
          route("/auth") {
            initAuthenticatedRoutes()
          }
        }
      }

    }.start(true)
  }

  logger.info("API Started!")
}