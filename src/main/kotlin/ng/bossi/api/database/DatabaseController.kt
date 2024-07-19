package ng.bossi.api.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ng.bossi.api.config.ConfigController
import ng.bossi.api.database.model.*
import ng.bossi.api.database.service.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.logging.Logger
import kotlin.math.log
import kotlin.system.exitProcess

object DatabaseController {

  private val config = ConfigController.config.databaseConfig
  private val logger = LoggerFactory.getLogger(DatabaseController::class.java)

  private val db by lazy {
    Database.connect(
      datasource = HikariDataSource(
        HikariConfig().apply {
          //jdbcUrl = "jdbc:postgresql://${config.host}:${config.port}/${config.database}"
          //driverClassName = "org.postgresql.Driver"
          //username = config.database
          //password = config.database
          jdbcUrl = "jdbc:sqlite:test.db"
          driverClassName = "org.sqlite.JDBC"
          maximumPoolSize = 3
          isReadOnly = false
        }
      )
    )
  }

  val applicationService by lazy { ApplicationService(db) }
  val featureFlagService by lazy { FeatureFlagService(db) }
  val resourceService by lazy { ResourceService(db) }
  val singleLicenseService by lazy { SingleLicenseService(db) }
  val versionService by lazy { VersionService(db) }

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

    logger.info("Creating Schema...")
    transaction {
      SchemaUtils.create(Applications, FeatureFlags, Resources, SingleLicenses, Versions)
    }

    logger.info("Loading Application Service...")
    applicationService

    logger.info("Loading Feature Flag Service...")
    featureFlagService

    logger.info("Loading Resource Service...")
    resourceService

    logger.info("Loading Single License Service...")
    singleLicenseService

    logger.info("Loading Version Service...")
    versionService
  }

}