package ch.skyfy.revival.data

import ch.skyfy.json5configlib.Validatable
import kotlinx.serialization.Serializable
import kotlin.reflect.jvm.internal.ReflectProperties.Val

@Serializable
data class Pos(
    val x: Int,
    val y: Int,
    val z: Int,
) : Validatable