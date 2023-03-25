package ch.skyfy.revival.persistent

import ch.skyfy.json5configlib.ConfigData
import ch.skyfy.revival.RevivalMod

object Persistents {
    @JvmField
    val DEATH_PLAYERS = ConfigData.invokeSpecial<DeadPlayers>(RevivalMod.PERSISTENTS_DIRECTORY.resolve("death_players.json5"), true)

}