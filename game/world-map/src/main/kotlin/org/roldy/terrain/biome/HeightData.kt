package org.roldy.terrain.biome

interface HeightData {
    val elevation: Float
    val temperature: Float
    val moisture: Float
}


infix fun HeightData.inRange(data: HeightData) =
    elevation <= data.elevation && moisture <= data.moisture && temperature <= data.temperature