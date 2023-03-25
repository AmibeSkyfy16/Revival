package ch.skyfy.revival.config

import ch.skyfy.json5configlib.ConfigData
import ch.skyfy.revival.RevivalMod


object Configs {
    @JvmField
    val CONFIG = ConfigData.invokeSpecial<Config>(RevivalMod.CONFIG_DIRECTORY.resolve("config.json5"), true)

//    @JvmField
//    val AVAILABLE_BLOCKS = ConfigData.invokeSpecial<AvailableBlocks>(BetterSnowGolemMod.CONFIG_DIRECTORY.resolve("available-blocks.json5"), true)
}
