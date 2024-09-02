package ng.bossi.api.database.service

import ng.bossi.api.model.Applications
import ng.bossi.api.model.FeatureFlag
import ng.bossi.api.model.FeatureFlagState
import ng.bossi.api.model.FeatureFlags
import org.jetbrains.exposed.sql.*

@Suppress("unused")
class FeatureFlagService(val database: Database) : DatabaseQuerying<FeatureFlag> {

  suspend fun create(entity: FeatureFlag): Long = dbQuery {
    FeatureFlags.insert {
      it[name] = entity.name
      it[application] = entity.application
      it[state] = entity.state
    }[FeatureFlags.id]
  }

  suspend fun read(name: String, application: Long): Pair<Long, FeatureFlag>? = dbQuery {
    (FeatureFlags innerJoin Applications)
      .selectAll()
      .where { (FeatureFlags.name eq name) and (FeatureFlags.application eq application) }
      .map { it[FeatureFlags.id] to (FeatureFlag.fromRow(it) ?: return@dbQuery null) }
      .singleOrNull()
  }

  suspend fun update(name: String, application: Long, state: FeatureFlagState): Boolean = dbQuery {
    FeatureFlags.update({ (FeatureFlags.name eq name) and (FeatureFlags.application eq application) }) {
      it[FeatureFlags.state] = state
    } > 0
  }

  suspend fun deleteById(id: Long): Boolean = dbQuery {
    FeatureFlags.deleteWhere { Op.build { FeatureFlags.id eq id } } > 0
  }

  suspend fun nameById(id: Long): Pair<String, Long>? = dbQuery {
    (FeatureFlags innerJoin Applications)
      .select(FeatureFlags.name, FeatureFlags.application)
      .where { FeatureFlags.id eq id }
      .map { it[FeatureFlags.name] to it[FeatureFlags.application] }
      .singleOrNull()
  }
}