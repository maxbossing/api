package ng.bossi.api.database.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ng.bossi.api.signing.ResponseSigning
import ng.bossi.api.utils.json
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

@Serializable
data class SingleLicense(val application: Long, val status: SingleLicenseStatus) {
  fun signable(): SignedSingleLicenseResponse = SignedSingleLicenseResponse(application, status, "")
  suspend fun sign(application: Long): SignedSingleLicenseResponse? =
    ResponseSigning.signAsResponse<SingleLicense, SignedSingleLicenseResponse, Json>(
      application,
      this,
      signable(),
      json
    )
}

@Serializable
data class SignedSingleLicenseResponse(
  val application: Long,
  val status: SingleLicenseStatus,
  override var sign: String
) : ResponseSigning.SignableResponse()

enum class SingleLicenseStatus { ENABLED, DISABLED, PAUSED }

object SingleLicenses : Table() {
  val id: Column<Long> = long("id").autoIncrement()
  val application: Column<Long> = reference("applicationId", Applications.id)
  val status: Column<SingleLicenseStatus> = enumeration("status", SingleLicenseStatus::class)

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "singleLicenseId")
}