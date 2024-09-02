package ng.bossi.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

enum class FeatureFlagState { ENABLED, DISABLED, DEPRECATED }

@Serializable
data class FeatureFlag(
  val name: String,
  val application: Long,
  val state: FeatureFlagState,
) {
  companion object : IResultRowMappable<FeatureFlag> {
    override fun fromRow(row: ResultRow): FeatureFlag? {
      try {
        return FeatureFlag(row[FeatureFlags.name], row[FeatureFlags.application], row[FeatureFlags.state])
      } catch (e: Exception) {
        e.printStackTrace()
        return null
      }
    }
  }
}

object FeatureFlags : Table() {
  val id: Column<Long> = long("featureFlagId").autoIncrement()
  val name: Column<String> = varchar("name", 255)
  val application: Column<Long> = reference("applicationId", Applications.id)
  val state: Column<FeatureFlagState> = enumeration<FeatureFlagState>("state")

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "featureFlagId")
}