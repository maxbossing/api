package ng.bossi.api.database.service

import ng.bossi.api.model.Applications
import ng.bossi.api.model.SingleLicense
import ng.bossi.api.model.SingleLicenseStatus
import ng.bossi.api.model.SingleLicenses
import org.jetbrains.exposed.sql.*
import java.util.*

@Suppress("unused")
class SingleLicenseService(val database: Database) : DatabaseQuerying<SingleLicense> {
  suspend fun create(application: Long, status: SingleLicenseStatus): Pair<Long, UUID> = dbQuery {
    val row = SingleLicenses.insert {
      it[SingleLicenses.application] = application
      it[SingleLicenses.status] = status
    }
    row[SingleLicenses.id] to row[SingleLicenses.key]
  }

  suspend fun readById(id: Long): SingleLicense? = dbQuery {
    (SingleLicenses innerJoin Applications).selectAll()
      .where { SingleLicenses.id eq id }
      .map { SingleLicense.fromRow(it) }
      .singleOrNull()
  }

  suspend fun updateById(id: Long, entity: SingleLicense): Boolean = dbQuery {
    SingleLicenses.update({ SingleLicenses.id eq id }) {
      it[status] = entity.status
    } > 0
  }

  suspend fun deleteById(id: Long): Boolean = dbQuery {
    SingleLicenses.deleteWhere { Op.build { SingleLicenses.id eq id } } > 0
  }

  suspend fun readByKey(key: UUID): SingleLicense? = dbQuery {
    (SingleLicenses innerJoin Applications)
      .selectAll()
      .where { SingleLicenses.key eq key }
      .map { SingleLicense.fromRow(it) }
      .singleOrNull()
  }

  suspend fun updateByKey(key: UUID, status: SingleLicenseStatus): Boolean = dbQuery {
    SingleLicenses.update({ SingleLicenses.key eq key }) {
      it[SingleLicenses.status] = status
    } > 0
  }

  suspend fun deleteByKey(key: UUID): Boolean = dbQuery {
    SingleLicenses.deleteWhere { Op.build { SingleLicenses.key eq key } } > 0
  }
}