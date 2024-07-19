package ng.bossi.api.utils

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.*
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.math.log

/**
 * Load a @[Serializable] [Any] from a [Path], overwriting it with [default] if not existing or errors happen during serialization
 *
 * @param T the object used to deserialize to
 * @param default Default object to write/return
 * @param stringFormat Deserializer to use
 * @param logger [Logger] to use to log errors/warnings
 *
 * @return The contents of the file deserialized as [T], or [default] if errors occured
 */
inline fun <reified T : @Serializable Any> Path.loadData(default: T, stringFormat: StringFormat, logger: Logger): T {
  return if (!exists()) {
    logger.warn("$fileName does not exist, creating...")
    createParentDirectories()
    createFile()
    writeText(stringFormat.encodeToString(default))
    default
  } else try {
    logger.info("Loading $fileName...")
    stringFormat.decodeFromString<T>(readText())
  } catch (_: Exception) {
    logger.warn("Failed to load $fileName, resetting file...")
    writeText(stringFormat.encodeToString(default))
    default
  }
}