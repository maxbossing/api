package ng.bossi.api.data.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIConfig(
    @SerialName("Database")
    val databaseConfig: DatabaseConfig = DatabaseConfig(),

    @SerialName("Discord")
    val discordConfig: DiscordConfig = DiscordConfig(),
)

@Serializable
data class DatabaseConfig(
    val host: String = "localhost",
    val port: Int = 5432,
    val database: String = "api",
    val username: String = "postgres",
    val password: String = "postgres",
)

@Serializable
data class DiscordConfig(
    val token: String = "",
    val logChannel: Long = 0,
    val adminUsers: Set<Long> = setOf(492297419736875009), /*Fuck you OAuth*/
)