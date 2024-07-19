package ng.bossi.api.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table


@Serializable
data class FeatureFlag(val name: String, val application: Long, val enabled: Boolean)

object FeatureFlags : Table() {
  val id: Column<Long> = long("id").autoIncrement()
  val name: Column<String> = varchar("name", 255)
  val application: Column<Long> = reference("applicationId", Applications.id)
  val enabled: Column<Boolean> = bool("enabled")

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "featureFlagId")
}