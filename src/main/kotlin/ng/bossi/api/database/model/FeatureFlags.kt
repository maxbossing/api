package ng.bossi.api.database.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ng.bossi.api.signing.ResponseSigning
import ng.bossi.api.utils.json
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table


@Serializable
data class FeatureFlag(val name: String, val application: Long, val enabled: Boolean) {

  fun signable(): SignedFeatureFlagResponse = SignedFeatureFlagResponse(name, application, enabled, "")
  suspend fun sign(application: Long): SignedFeatureFlagResponse? =
    ResponseSigning.signAsResponse<FeatureFlag, SignedFeatureFlagResponse, Json>(application, this, signable(), json)
}

@Serializable
data class SignedFeatureFlagResponse(
  val name: String,
  val application: Long,
  val enabled: Boolean,
  override var sign: String
) : ResponseSigning.SignableResponse()

object FeatureFlags : Table() {
  val id: Column<Long> = long("id").autoIncrement()
  val name: Column<String> = varchar("name", 255)
  val application: Column<Long> = reference("applicationId", Applications.id)
  val enabled: Column<Boolean> = bool("enabled")

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "featureFlagId")
}