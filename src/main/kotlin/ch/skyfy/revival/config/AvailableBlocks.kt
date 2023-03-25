package ch.skyfy.revival.config

import ch.skyfy.json5configlib.Validatable
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.Serializable

@Serializable
data class AvailableBlocks(
    @SerialComment("A list block translation key you can use in the config.json5")
    val allowedBlocks: MutableSet<String> = mutableSetOf()
) : Validatable