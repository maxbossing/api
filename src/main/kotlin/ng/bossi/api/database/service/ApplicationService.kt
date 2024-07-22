package ng.bossi.api.database.service

import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.*
import ng.bossi.api.signing.ResponseSigning
import org.jetbrains.exposed.sql.*

class ApplicationService(val database: Database) : IDatabaseService<Long, Application> {
  override suspend fun create(entity: Application): Long = dbQuery {
    Applications.insert {
      it[name] = entity.name
      it[key] = entity.key
    }[Applications.id]
  }

  override suspend fun read(id: Long): Application? = dbQuery {
    Applications.selectAll()
      .where { Applications.id eq id }
      .map { Application(it[Applications.name], it[Applications.key]) }
      .singleOrNull()
  }

  override suspend fun update(id: Long, entity: Application): Boolean = dbQuery {
    val successful = Applications.update({ Applications.id eq id }) {
      it[name] = entity.name
      it[key] = entity.key
    } > 0

    ResponseSigning.keyCache.invalidate(id)

    successful
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    val successful = Applications.deleteWhere { Op.build { Applications.id eq id } } > 0
    ResponseSigning.keyCache.invalidate(id)
    successful
  }

  suspend fun deleteByName(name: String): Boolean = delete(nameToId(name)?: -1)

  suspend fun getVersions(id: Long): List<Version> = dbQuery {
    (Applications innerJoin Versions).selectAll()
      .where { Applications.id eq id }
      .mapNotNull { DatabaseController.versionService.read(it[Versions.id]) }
  }

  suspend fun getCurrentVersion(id: Long): Version? = dbQuery { transaction ->
    transaction.addLogger(StdOutSqlLogger)
    (Applications innerJoin Versions)
      .select(Versions.id)
      .where { (Applications.id eq id) and (Versions.status eq VersionStatus.CURRENT) }
      .map { DatabaseController.versionService.read(it[Versions.id])}
      .singleOrNull()
  }

  suspend fun getByName(name: String): Pair <Long, Application>? = dbQuery {
    it.addLogger(StdOutSqlLogger)
    Applications
      .selectAll()
      .where {Applications.name eq name }
      .map { it[Applications.id] to Application(it[Applications.name], it[Applications.key]) }
      .singleOrNull()
  }

  suspend fun nameToId(name: String): Long? = dbQuery {
    Applications.select(Applications.id).where { Applications.name eq name }.singleOrNull()?.get(Applications.id)
  }

  suspend fun updateByName(name: String, key: ByteArray): Boolean {
    return update(nameToId(name) ?: return false, Application(name = name, key = key))
  }

}