package ng.bossi.api.data.database.service

import ng.bossi.api.data.database.model.Applications
import ng.bossi.api.data.database.model.Application
import org.jetbrains.exposed.sql.*

class ApplicationService(val database: Database): IDatabaseService<Long, Application> {
  override suspend fun create(entity: Application): Long = dbQuery {
    Applications.insert {
      it[name] = entity.name
      it[key] = entity.key
    }[Applications.id]
  }

  override suspend fun read(id: Long): Application? = dbQuery {
    Applications.selectAll()
      .where { Applications.id eq id}
      .map { Application(it[Applications.name], it[Applications.key]) }
      .singleOrNull()
  }

  override suspend fun update(id: Long, entity: Application): Boolean = dbQuery {
    Applications.update({ Applications.id eq id }) {
      it[name] = entity.name
      it[key] = entity.key
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
      Applications.deleteWhere { Op.build { Applications.id eq id } } > 0
  }
}