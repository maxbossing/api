package ng.bossi.api.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.security.Permission

object BearerTokens : Table() {
  val id: Column<Long> = long("tokenId").autoIncrement()
  val hash: Column<ByteArray> = binary("hash", 64).uniqueIndex()
  val permission: Column<Long> = long("permission")

  override val primaryKey = PrimaryKey(id, name = "tokenId")
}