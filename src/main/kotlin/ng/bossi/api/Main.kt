package ng.bossi.api

import io.ktor.http.auth.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ng.bossi.api.config.ConfigController
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.FeatureFlag
import ng.bossi.api.database.model.Resource
import ng.bossi.api.database.model.SingleLicense
import ng.bossi.api.database.model.SingleLicenseStatus
import ng.bossi.api.http.initRoutes
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.xml.crypto.Data

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
            install(Routing) {
                initRoutes()
            }
        }.start(true)
    }

    logger.info("API Started!")
}