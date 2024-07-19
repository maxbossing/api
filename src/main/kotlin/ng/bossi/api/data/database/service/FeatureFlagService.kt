package ng.bossi.api.data.database.service

import ng.bossi.api.data.database.model.Applications
import ng.bossi.api.data.database.model.FeatureFlag
import ng.bossi.api.data.database.model.FeatureFlags
import org.jetbrains.exposed.sql.*

class FeatureFlagService(val database: Database) : IDatabaseService<Long, FeatureFlag> {
  override suspend fun create(entity: FeatureFlag): Long = dbQuery {
    FeatureFlags.insert {
      it[FeatureFlags.name] = entity.name
      it[FeatureFlags.application] = entity.application
      it[FeatureFlags.enabled] = entity.enabled
    }[FeatureFlags.id]
  }

  override suspend fun read(id: Long): FeatureFlag? = dbQuery {
    (FeatureFlags innerJoin Applications)
      .selectAll()
      .where { FeatureFlags.id eq id}
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
      it[FeatureFlags.name] = entity.name
      it[FeatureFlags.application] = entity.application
      it[FeatureFlags.enabled] = entity.enabled
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    FeatureFlags.deleteWhere { Op.build { FeatureFlags.id eq id } } > 0
  }

}