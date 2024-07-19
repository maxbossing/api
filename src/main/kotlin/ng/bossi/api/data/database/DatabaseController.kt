package ng.bossi.api.data.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ng.bossi.api.data.config.ConfigController
import ng.bossi.api.data.database.model.*
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.exitProcess

object DatabaseController {
  private val logger = LogManager.getLogger(DatabaseController::class)

  private val config = ConfigController.config.databaseConfig

  private val db by lazy {
    Database.connect(
      datasource = HikariDataSource(
        HikariConfig().apply {
          jdbcUrl = "jdbc:postgresql://${config.host}:${config.port}/${config.database}"
          driverClassName = "org.postgresql.Driver"
          username = config.database
          password = config.database
          maximumPoolSize = 3
          isReadOnly = false
        }
      )
    )
  }


  init {
    logger.info("Initializing database...")

    if (config.username.isEmpty()) {
      logger.error("Username cannot be empty!")
      exitProcess(1)
    }
    if (config.password.isEmpty()) {
      logger.error("Password cannot be empty!")
      exitProcess(1)
    }
    if (config.host.isEmpty()) {
      logger.error("Host cannot be empty!")
      exitProcess(1)
    }
    if(config.database.isEmpty()) {
      logger.error("Database cannot be empty!")
      exitProcess(1)
    }

    logger.info("Trying to connect...")
    db
    logger.info("Connected to database!")

    logger.info("Creating Schema...")
    transaction {
      SchemaUtils.create(Applications, FeatureFlags, Resources, SingleLicenses, Versions, inBatch = true)
    }
    logger.info("Created Schema!")


  }
}