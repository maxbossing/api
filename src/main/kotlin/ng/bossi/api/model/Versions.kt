package ng.bossi.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Serializable
data class Version(
  val version: String,
  val codename: String,
  val application: Long,
  val status: VersionStatus,
  val resource: Long,
) {
  companion object : IResultRowMappable<Version> {
    override fun fromRow(row: ResultRow): Version? {
      try {
        return Version(
          row[Versions.version],
          row[Versions.codename],
          row[Versions.application],
          row[Versions.status],
          row[Versions.resource],
        )
      } catch (e: Exception) {
        e.printStackTrace()
        return null
      }
    }
  }
}

@Suppress("unused")
enum class VersionStatus { CURRENT, SUPPORTED, UNSUPPORTED }

object Versions : Table() {
  val id: Column<Long> = long("versionId").autoIncrement()
  val version: Column<String> = varchar("version", 255)
  val codename: Column<String> = varchar("codename", 255)
  val application: Column<Long> = reference("applicationId", Applications.id)
  val status: Column<VersionStatus> = enumeration("status", VersionStatus::class)
  val resource: Column<Long> = reference("resource", Resources.id)

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "versionId")

}