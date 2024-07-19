package ng.bossi.api.data.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

@Serializable
data class SingleLicense(val application: Long, val status: SingleLicenseStatus, )

enum class SingleLicenseStatus { ENABLED, DISABLED, PAUSED }

object SingleLicenses : Table() {
  val id: Column<Long> = long("id").autoIncrement()
  val application: Column<Long> = reference("applicationId", Applications.id)
  val status: Column<SingleLicenseStatus> = enumeration("status", SingleLicenseStatus::class)

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "singleLicenseId")
}