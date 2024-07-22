package ng.bossi.api.model

import kotlinx.serialization.Serializable
import ng.bossi.api.utils.UUIDSerializer
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.util.*

@Serializable
data class SingleLicense(
  val key: @Serializable(with = UUIDSerializer::class) UUID,
  val application: Long,
  val status: SingleLicenseStatus,
) {
  companion object : IResultRowMappable<SingleLicense> {
    override fun fromRow(row: ResultRow): SingleLicense? {
      try {
        return SingleLicense(row[SingleLicenses.key], row[Applications.id], row[SingleLicenses.status])
      } catch (e: Exception) {
        e.printStackTrace()
        return null
      }
    }
  }
}


@Suppress("unused")
enum class SingleLicenseStatus { ENABLED, DISABLED, PAUSED }

object SingleLicenses : Table() {
  val id: Column<Long> = long("singleLicenseId").autoIncrement()
  val key: Column<UUID> = uuid("key").uniqueIndex().autoGenerate()
  val application: Column<Long> = reference("applicationId", Applications.id)
  val status: Column<SingleLicenseStatus> = enumeration("status", SingleLicenseStatus::class)

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "singleLicenseId")
}