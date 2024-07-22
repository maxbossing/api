package ng.bossi.api.database.service

import ng.bossi.api.model.*
import org.jetbrains.exposed.sql.*

@Suppress("unused")
class VersionService(val database: Database) : DatabaseQuerying<Version> {
  suspend fun createById(entity: Version): Long? = dbQuery {
    if (readByName(entity.version, entity.application) != null)
      return@dbQuery null

    Versions.insert {
      it[version] = entity.version
      it[codename] = entity.codename
      it[application] = entity.application
      it[status] = entity.status
      it[resource] = entity.resource
    }[Versions.id]
  }

  suspend fun readById(id: Long): Version? = dbQuery {
    (Versions innerJoin Applications innerJoin Resources)
      .selectAll()
      .where { Versions.id eq id }
      .map { Version.fromRow(it) }
      .singleOrNull()
  }

  suspend fun readByName(version: String, application: Long): Version? = dbQuery { it ->
    it.addLogger(StdOutSqlLogger)
    (Versions innerJoin Applications innerJoin Resources)
      .selectAll()
      .where { (Versions.version eq version) and (Applications.id eq application) }
      .map { Version.fromRow(it) }
      .singleOrNull()
  }

  suspend fun updateById(id: Long, resource: Long? = null, status: VersionStatus? = null): Boolean = dbQuery {
    (Versions innerJoin Applications innerJoin Resources)
      .update({ Versions.id eq id }) {
        status?.let { s -> it[Versions.status] = s }
        resource?.let { r -> it[Versions.resource] = r }
      } > 0
  }

  suspend fun updateByName(
    version: String,
    application: Long,
    resource: Long? = null,
    status: VersionStatus? = null,
  ): Boolean = dbQuery {
    (Versions innerJoin Applications innerJoin Resources)
      .update({ (Versions.version eq version) and (Applications.id eq application) }) {
        status?.let { s -> it[Versions.status] = s }
        resource?.let { r -> it[Versions.resource] = r }
      } > 0
  }

  suspend fun deleteById(id: Long): Boolean = dbQuery {
    Versions.deleteWhere { Op.build { Versions.id eq id } } > 0
  }

  suspend fun deleteByName(version: String, application: Long): Boolean = dbQuery {
    Versions.deleteWhere { Op.build { (Versions.version eq version) and (Applications.id eq application) } } > 0
  }
}
