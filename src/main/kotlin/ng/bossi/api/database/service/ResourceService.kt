package ng.bossi.api.database.service

import ng.bossi.api.database.model.Resource
import ng.bossi.api.database.model.Resources
import org.jetbrains.exposed.sql.*

class ResourceService(val database: Database) : IDatabaseService<Long, Resource> {
  override suspend fun create(entity: Resource): Long  = dbQuery {
    Resources.insert {
      it[Resources.name] = entity.name
      it[Resources.hash] = entity.hash
      it[Resources.size] = entity.size
      it[Resources.url] = entity.url
    }[Resources.id]
  }

  override suspend fun read(id: Long): Resource? = dbQuery {
    Resources.selectAll()
      .where { Resources.id eq id }
      .map { Resource(
        name = it[Resources.name],
        hash = it[Resources.hash],
        size = it[Resources.size],
        url = it[Resources.url]
      )}.singleOrNull()
  }

  override suspend fun update(id: Long, entity: Resource): Boolean = dbQuery {
    Resources.update({ Resources.id eq id}) {
      it[Resources.name] = entity.name
      it[Resources.hash] = entity.hash
      it[Resources.size] = entity.size
      it[Resources.url] = entity.url
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    Resources.deleteWhere { Op.build { Resources.id eq id } } > 0
  }

}