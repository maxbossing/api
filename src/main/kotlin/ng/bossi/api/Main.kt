package ng.bossi.api

import ng.bossi.api.config.ConfigController
import ng.bossi.api.database.DatabaseController
import org.apache.logging.log4j.LogManager

val LOGGER = LogManager.getLogger("API")


fun main() {
    LOGGER.info("Starting API...")

    ConfigController
    DatabaseController

}