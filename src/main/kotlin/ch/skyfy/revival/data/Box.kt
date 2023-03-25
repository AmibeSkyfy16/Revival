package ch.skyfy.revival.data

import ch.skyfy.json5configlib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Box(
    // The number of block in each direction that will define the square
    val size: Int,

    // x, y, z  is the middle position
    val x: Int,
    val y: Int,
    val z: Int,
) : Validatable