package ng.bossi.api.database.service

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface IDatabaseService <I, T: @Serializable Any> {
  suspend fun <T> dbQuery(block: suspend (Transaction) -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block(this) }

  suspend fun create(entity: T): I
  suspend fun read(id: I): T?
  suspend fun update(id: I, entity: T) : Boolean
  suspend fun delete(id: I) : Boolean
}