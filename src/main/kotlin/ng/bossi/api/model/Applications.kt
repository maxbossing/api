package ng.bossi.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


@Serializable
data class Application(val name: String, val key: ByteArray) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Application

    if (name != other.name) return false
    if (!key.contentEquals(other.key)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = 31 + name.hashCode()
    result = 31 * result + key.contentHashCode()
    return result
  }

  companion object : IResultRowMappable<Application> {
    override fun fromRow(row: ResultRow): Application? {
      try {
        return Application(row[Applications.name], row[Applications.key])
      } catch (e: Exception) {
        e.printStackTrace()
        return null
      }
    }
  }
}

object Applications : Table() {
  val id: Column<Long> = long("applicationId").autoIncrement()
  val name: Column<String> = varchar("name", 255).uniqueIndex()
  val key: Column<ByteArray> = binary("privateKey", 1701)

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "applicationId")
}
