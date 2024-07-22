package ng.bossi.api.database.service

import ng.bossi.api.model.Resource
import ng.bossi.api.model.Resources
import org.jetbrains.exposed.sql.*

@Suppress("unused")
class ResourceService(val database: Database) : DatabaseQuerying<Resource> {
  suspend fun create(entity: Resource): Long = dbQuery {
    Resources.insert {
      it[name] = entity.name
      it[hash] = entity.hash
      it[size] = entity.size
      it[url] = entity.url
    }[Resources.id]
  }

  suspend fun read(id: Long): Resource? = dbQuery {
    Resources.selectAll()
      .where { Resources.id eq id }
      .map {
        Resource(
          name = it[Resources.name],
          hash = it[Resources.hash],
          size = it[Resources.size],
          url = it[Resources.url]
        )
      }.singleOrNull()
  }

  suspend fun update(id: Long, entity: Resource): Boolean = dbQuery {
    Resources.update({ Resources.id eq id }) {
      it[name] = entity.name
      it[hash] = entity.hash
      it[size] = entity.size
      it[url] = entity.url
    } > 0
  }

  suspend fun delete(id: Long): Boolean = dbQuery {
    Resources.deleteWhere { Op.build { Resources.id eq id } } > 0
  }
}