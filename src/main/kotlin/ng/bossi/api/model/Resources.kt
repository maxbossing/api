package ng.bossi.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Serializable
data class Resource(
  val name: String,
  val hash: ByteArray,
  val size: Long,
  val url: String,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Resource

    if (name != other.name) return false
    if (!hash.contentEquals(other.hash)) return false
    if (size != other.size) return false
    if (url != other.url) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + hash.contentHashCode()
    result = 31 * result + size.hashCode()
    result = 31 * result + url.hashCode()
    return result
  }

  companion object : IResultRowMappable<Resource> {
    override fun fromRow(row: ResultRow): Resource? {
      try {
        return Resource(row[Resources.name], row[Resources.hash], row[Resources.size], row[Resources.url])
      } catch (e: Exception) {
        e.printStackTrace()
        return null
      }
    }
  }
}

object Resources : Table() {
  val id: Column<Long> = long("resourceId").autoIncrement()
  val name: Column<String> = varchar("name", 1024)
  val hash: Column<ByteArray> = binary("hash", 16)
  val size: Column<Long> = long("size")
  val url: Column<String> = varchar("url", 2048)

  override val primaryKey: PrimaryKey = PrimaryKey(id, name = "resourceId")
}