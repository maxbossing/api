package ng.bossi.api.data.config

import com.akuleshov7.ktoml.Toml
import ng.bossi.api.utils.loadData
import ng.bossi.api.utils.toml
import org.apache.logging.log4j.LogManager
import kotlin.io.path.Path

object ConfigController {
  private val configPath = Path("config.toml")
  private val logger = LogManager.getLogger(ConfigController::class)

  val config = configPath.loadData<APIConfig>(APIConfig(), toml, logger)
}