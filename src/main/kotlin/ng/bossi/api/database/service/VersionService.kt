package ng.bossi.api.database.service

import ng.bossi.api.database.model.Applications
import ng.bossi.api.database.model.Resources
import ng.bossi.api.database.model.Version
import ng.bossi.api.database.model.Versions
import org.jetbrains.exposed.sql.*

class VersionService(val database: Database) : IDatabaseService<Long, Version> {
  override suspend fun create(entity: Version): Long = dbQuery {
    Versions.insert {
      it[version] = entity.version
      it[codename] = entity.codename
      it[application] = entity.application
      it[status] = entity.status
      it[resource] = entity.resource
    }[Versions.id]
  }

  override suspend fun read(id: Long): Version? = dbQuery { transaction ->
    transaction.addLogger(StdOutSqlLogger)
    (Versions innerJoin Applications innerJoin Resources)
      .selectAll()
      .where { Versions.id eq id }
      .map {
        Version(
          version = it[Versions.version],
          codename = it[Versions.codename],
          application = it[Applications.id],
          status = it[Versions.status],
          resource = it[Resources.id]
        )
      }.singleOrNull()
  }

  override suspend fun update(id: Long, entity: Version): Boolean = dbQuery {
    Versions.update({ Versions.id eq id }) {
      it[version] = entity.version
      it[codename] = entity.codename
      it[application] = entity.application
      it[status] = entity.status
      it[resource] = entity.resource
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    Versions.deleteWhere { Op.build { Versions.id eq id } } > 0
  }
}
