package ng.bossi.api.utils

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
  isLenient = true
  encodeDefaults = true
  ignoreUnknownKeys = true
  decodeEnumsCaseInsensitive = true
}

val toml = Toml(
  TomlInputConfig(
    allowEmptyToml = true,
    ignoreUnknownNames = true,
    allowEmptyValues = true,
    allowNullValues = true,
  ),
  TomlOutputConfig(
    indentation = TomlIndentation.NONE,
    ignoreDefaultValues = false,
    ignoreNullValues = false,

    )
)