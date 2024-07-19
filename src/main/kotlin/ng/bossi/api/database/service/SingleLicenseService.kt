package ng.bossi.api.database.service

import ng.bossi.api.database.model.Applications
import ng.bossi.api.database.model.SingleLicense
import ng.bossi.api.database.model.SingleLicenses
import org.jetbrains.exposed.sql.*

class SingleLicenseService(val database: Database): IDatabaseService<Long, SingleLicense> {
  override suspend fun create(entity: SingleLicense): Long = dbQuery {
    SingleLicenses.insert {
      it[SingleLicenses.application] = entity.application
      it[SingleLicenses.status] = entity.status
    }[SingleLicenses.id]
  }

  override suspend fun read(id: Long): SingleLicense? = dbQuery {
    (SingleLicenses innerJoin Applications).selectAll()
      .where { SingleLicenses.id eq id }
      .map { SingleLicense(
        application = it[Applications.id],
        status = it[SingleLicenses.status]
      )}.singleOrNull()
  }

  override suspend fun update(id: Long, entity: SingleLicense): Boolean = dbQuery {
    SingleLicenses.update({ SingleLicenses.id eq id }) {
      it[SingleLicenses.application] = entity.application
      it[SingleLicenses.status] = entity.status
    } > 0
  }

  override suspend fun delete(id: Long): Boolean = dbQuery {
    SingleLicenses.deleteWhere { Op.build { SingleLicenses.id eq id } } > 0
  }
}