package ng.bossi.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

interface IResultRowMappable<T : @Serializable Any> {
  fun fromRow(row: ResultRow): T?
}