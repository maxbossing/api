package ng.bossi.api.config

import ng.bossi.api.utils.loadData
import ng.bossi.api.utils.toml
import org.slf4j.LoggerFactory
import kotlin.io.path.Path

object ConfigController {
  private val configPath = Path("config.toml")
  private val logger = LoggerFactory.getLogger(ConfigController::class.java)

  val config by lazy { configPath.loadData<APIConfig>(APIConfig(), toml, logger) }

  init {
    logger.info("Initializing config...")
    config
  }
}