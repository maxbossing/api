package ng.bossi.api.config

import ng.bossi.api.LOGGER
import ng.bossi.api.utils.loadData
import ng.bossi.api.utils.toml
import org.apache.logging.log4j.LogManager
import kotlin.io.path.Path

object ConfigController {
  private val configPath = Path("config.toml")
  private val logger = LogManager.getLogger(ConfigController::class)

  val config by lazy { configPath.loadData<APIConfig>(APIConfig(), toml, logger) }

  init {
    LOGGER.info("Initializing config...")
    config
  }
}