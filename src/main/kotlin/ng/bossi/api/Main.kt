package ng.bossi.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ng.bossi.api.config.ConfigController
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.*
import ng.bossi.api.database.model.Application
import ng.bossi.api.http.initRoutes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("API")

lateinit var ktorServer: ApplicationEngine

@Suppress("unused")
suspend fun main(args: Array<String>) = coroutineScope {

    logger.info("Starting API...")

    ConfigController
    DatabaseController

    logger.info("Starting KTOR Server...")

    launch {
        ktorServer = embeddedServer(CIO, 8080) {
            install(ContentNegotiation) {
                json()
            }
            install(Routing) {
                initRoutes()
            }
        }.start(true)
    }

    logger.info("API Started!")
}