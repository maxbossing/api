package ng.bossi.api.database.service

import ng.bossi.api.database.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class VersionService(val database: Database) : IDatabaseService<Long, Version> {
  override suspend fun create(entity: Version): Long = dbQuery {
    Versions.insert {
      it[Versions.version] = entity.version
      it[Versions.codename] = entity.codename
      it[Versions.application] = entity.application
      it[Versions.status] = entity.status
      it[Versions.resource] = entity.resource
    }[Versions.id]
  }

  override suspend fun read(id: Long): Version? = dbQuery { transaction ->
    transaction.addLogger(StdOutSqlLogger)
    (Versions innerJoin Applications innerJoin Resources)
      .selectAll()
      .where { Versions.id eq id }
      .map { Version(
        version = it[Versions.version],
        codename = it[Versions.codename],
        application = it[Applications.id],
        status = it[Versions.status],
        resource = it[Resources.id]
      )}.singleOrNull()
  }

  override suspend fun update(id: Long, entity: Version): Boolean = dbQuery {
    Versions.update({ Versions.id eq id }) {
      it[Versions.version] = entity.version
      it[Versions.codename] = entity.codename
      it[Versions.application] = entity.application
      it[Versions.status] = entity.status
      it[Versions.resource] = entity.resource
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    Versions.deleteWhere { Op.build { Versions.id eq id } } > 0
  }
}
