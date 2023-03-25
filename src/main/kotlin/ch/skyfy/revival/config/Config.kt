package ch.skyfy.revival.config

import ch.skyfy.json5configlib.Validatable
import ch.skyfy.revival.data.Box
import kotlinx.serialization.Serializable

@Serializable
data class Config(

    // We can use this mod in a non-hardcore server (player that die will become spectator for a time)
    val onlyUseOnHardcore: Boolean = true,

    // How many times a player can be revived
    val maximumRevive: Int = 5,

    val maximumWanderTimeInMinute: Int = 5,

    // Will define a square where the ghost cannot go beyond
    val maximumWanderBox: Int = 10,

    val revivedPlayerHealth: Float = 2f
) : Validatable
