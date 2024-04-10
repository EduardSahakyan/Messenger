package data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val temperature: String,
    val windSpeed: Double,
    val airQuality: String,
    val sender: String,
    val date: String
)
