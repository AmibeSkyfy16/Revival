package ch.skyfy.revival.persistent

import ch.skyfy.json5configlib.Validatable
import ch.skyfy.revival.data.Pos
import kotlinx.serialization.Serializable

@Serializable
data class DeadPlayers(
    val list: MutableList<DeadPlayer> = mutableListOf()
) : Validatable

@Serializable
data class DeadPlayer(
    val playerNameWithUUID: String,
    val deathLocation: Pos,
    val deathTimestamp: Long,
) : Validatable