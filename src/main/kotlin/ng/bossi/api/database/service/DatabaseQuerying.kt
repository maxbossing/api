package ng.bossi.api.database.service

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface DatabaseQuerying<T : @Serializable Any> {
  suspend fun <T> dbQuery(block: suspend (Transaction) -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block(this) }
}