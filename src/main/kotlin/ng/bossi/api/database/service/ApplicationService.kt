package ng.bossi.api.database.service

import ng.bossi.api.model.*
import ng.bossi.api.signing.ResponseSigning
import org.jetbrains.exposed.sql.*

@Suppress("unused")
class ApplicationService(private val database: Database) : DatabaseQuerying<Application> {
  suspend fun create(entity: Application): Long = dbQuery {
    Applications.insert {
      it[name] = entity.name
      it[key] = entity.key
    }[Applications.id]
  }

  suspend fun readById(id: Long): Application? = dbQuery {
    Applications.selectAll()
      .where { Applications.id eq id }
      .map { Application.fromRow(it) }
      .singleOrNull()
  }

  suspend fun readByName(name: String): Pair<Long, Application>? = dbQuery {
    Applications.selectAll()
      .where { Applications.name eq name }
      .map { it[Applications.id] to (Application.fromRow(it) ?: return@dbQuery null) }
      .singleOrNull()
  }

  suspend fun updateById(id: Long, key: ByteArray): Boolean = dbQuery {
    Applications.update({ Applications.id eq id }) {
      it[Applications.key] = key
    } > 0
  }

  suspend fun deleteById(id: Long): Boolean = dbQuery {
    val successful = Applications.deleteWhere { Op.build { Applications.id eq id } } > 0
    ResponseSigning.keyCache.invalidate(id)
    successful
  }


  suspend fun getNewestVersion(application: Long): Version? = dbQuery {
    (Versions innerJoin Applications)
      .selectAll()
      .where { (Versions.application eq application) and (Versions.status eq VersionStatus.CURRENT) }
      .map { Version.fromRow(it) }
      .singleOrNull()
  }

}