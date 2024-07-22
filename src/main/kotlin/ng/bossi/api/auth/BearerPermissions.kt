package ng.bossi.api.auth

import io.ktor.server.auth.*

enum class BearerPermissions(val bit: Long) {

  APPLICATION_CREATE(1 shl 0),
  APPLICATION_DELETE(1 shl 1),
  APPLICATION_UPDATE(1 shl 2),

  VERSION_CREATE(1 shl 5),
  VERSION_DELETE(1 shl 7),
  VERSION_UPDATE(1 shl 8),

  FEATUREFLAG_CREATE(1 shl 10),
  FEATUREFLAG_DELETE(1 shl 11),
  FEATUREFLAG_UPDATE(1 shl 12),

  SINGLELICENSE_CREATE(1 shl 15),
  SINGLELICENSE_DELETE(1 shl 16),
  SINGLELICENSE_UPDATE(1 shl 17),

  RESOURCE_CREATE(1 shl 20),
  RESOURCE_DELETE(1 shl 21),
  RESOURCE_UPDATE(1 shl 22),

  ALL(1 shl 63)
  ;
  operator fun plus(other: BearerPermissions): Long = bit or other.bit
  operator fun minus(other: BearerPermissions): Long = bit and other.bit.inv()
}

fun Long.permission(permission: Long): Boolean = (this and permission) != 0L
fun Long.permission(permission: BearerPermissions): Boolean = (this and permission.bit) != 0L

data class BearerPermission(
  val application: ApplicationPermissions,
  val version: VersionPermissions,
  val singleLicense: SingleLicensePermissions,
  val featureFlag: FeatureFlagPermissions,
): Principal {
  companion object {
    fun fromLong(int: Long): BearerPermission = BearerPermission(
      application = ApplicationPermissions.fromLong(int),
      version = VersionPermissions.fromLong(int),
      singleLicense = SingleLicensePermissions.fromLong(int),
      featureFlag = FeatureFlagPermissions.fromLong(int)
    )
  }
}

data class ApplicationPermissions(
  val create: Boolean = false,
  val delete: Boolean = false,
  val updateKey: Boolean = false
) {
  companion object {
    fun fromLong(int: Long): ApplicationPermissions = ApplicationPermissions(
      create = int.permission(BearerPermissions.APPLICATION_CREATE),
      delete = int.permission(BearerPermissions.APPLICATION_DELETE),
      updateKey = int.permission(BearerPermissions.APPLICATION_UPDATE)
    )
  }
}

data class VersionPermissions(
  val create: Boolean = false,
  val delete: Boolean = false,
  val updateState: Boolean = false
) {
  companion object {
    fun fromLong(int: Long): VersionPermissions = VersionPermissions(
      create = int.permission(BearerPermissions.VERSION_CREATE),
      delete = int.permission(BearerPermissions.VERSION_DELETE),
      updateState = int.permission(BearerPermissions.VERSION_UPDATE)
    )
  }
}

data class FeatureFlagPermissions(
  val create: Boolean = false,
  val delete: Boolean = false,
  val updateState: Boolean = false
) {
  companion object {
    fun fromLong(int: Long): FeatureFlagPermissions = FeatureFlagPermissions(
      create = int.permission(BearerPermissions.FEATUREFLAG_CREATE),
      delete = int.permission(BearerPermissions.FEATUREFLAG_DELETE),
      updateState = int.permission(BearerPermissions.FEATUREFLAG_UPDATE)
    )
  }
}

data class SingleLicensePermissions(
  val create: Boolean = false,
  val delete: Boolean = false,
  val updateStatus: Boolean = false
) {
  companion object {
    fun fromLong(int: Long): SingleLicensePermissions = SingleLicensePermissions(
      create = int.permission(BearerPermissions.SINGLELICENSE_CREATE),
      delete = int.permission(BearerPermissions.SINGLELICENSE_DELETE),
      updateStatus = int.permission(BearerPermissions.SINGLELICENSE_UPDATE)
    )
  }
}

