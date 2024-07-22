package ng.bossi.api.database.service

import ng.bossi.api.database.model.Applications
import ng.bossi.api.database.model.FeatureFlag
import ng.bossi.api.database.model.FeatureFlags
import org.jetbrains.exposed.sql.*
import java.awt.geom.PathIterator

class FeatureFlagService(val database: Database) : IDatabaseService<Long, FeatureFlag> {
  override suspend fun create(entity: FeatureFlag): Long = dbQuery {
    FeatureFlags.insert {
      it[name] = entity.name
      it[application] = entity.application
      it[enabled] = entity.enabled
    }[FeatureFlags.id]
  }

  override suspend fun read(id: Long): FeatureFlag? = dbQuery {
    (FeatureFlags innerJoin Applications)
      .selectAll()
      .where { FeatureFlags.id eq id }
      .map {
        FeatureFlag(
          name = it[FeatureFlags.name],
          application = it[Applications.id],
          enabled = it[FeatureFlags.enabled],
        )
      }.singleOrNull()
  }

  override suspend fun update(id: Long, entity: FeatureFlag): Boolean = dbQuery {
    FeatureFlags.update({ FeatureFlags.id eq id }) {
      it[name] = entity.name
      it[application] = entity.application
      it[enabled] = entity.enabled
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    FeatureFlags.deleteWhere { Op.build { FeatureFlags.id eq id } } > 0
  }


  suspend fun nameToId(name: String): Long? = dbQuery {
    FeatureFlags.select(FeatureFlags.id).where { FeatureFlags.name eq name }.singleOrNull()?.get(FeatureFlags.id)
  }
  suspend fun readByName(name: String): Pair<Long, FeatureFlag>? {
    val id = nameToId(name) ?: return null
    return id to read(id)!!
  }

}