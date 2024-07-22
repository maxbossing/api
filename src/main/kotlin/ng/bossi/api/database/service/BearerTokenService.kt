package ng.bossi.api.database.service

import io.ktor.util.*
import ng.bossi.api.model.BearerTokens
import ng.bossi.api.utils.UUIDSerializer
import org.jetbrains.exposed.sql.*
import java.security.MessageDigest
import java.util.UUID
import kotlin.random.Random

class BearerTokenService(val database: Database): DatabaseQuerying<ByteArray> {
  suspend fun create(permissions: Long): String = dbQuery {
    val bytes = Random.Default.nextBytes(32)
    val hash = MessageDigest.getInstance("SHA-512").digest(bytes)
    BearerTokens.insert {
      it[BearerTokens.hash] = hash
      it[permission] = permissions
    }
    bytes.encodeBase64()
  }

  suspend fun delete(id: Long): Boolean = dbQuery {
    BearerTokens.deleteWhere { Op.build { BearerTokens.id eq id } } > 0
  }

  suspend fun update(id: Long, permissions: Long): Boolean = dbQuery {
    BearerTokens.update({ BearerTokens.id eq id }) {
      it[BearerTokens.permission] = permissions
    } > 0
  }

  suspend fun getId(token: String): Long? = dbQuery {
    (BearerTokens
      .select(BearerTokens.id)
      .where { BearerTokens.hash eq MessageDigest.getInstance("SHA-512").digest(token.encodeToByteArray()) }
      .singleOrNull()?: return@dbQuery null)[BearerTokens.id]
  }

  suspend fun getPermissions(token: String): Long? = dbQuery {
    (BearerTokens
      .select(BearerTokens.permission)
      .where { BearerTokens.hash eq MessageDigest.getInstance("SHA-512").digest(token.encodeToByteArray()) }
      .singleOrNull()?: return@dbQuery null)[BearerTokens.permission]
  }
  suspend fun count(): Long = dbQuery { BearerTokens.selectAll().count() }
}