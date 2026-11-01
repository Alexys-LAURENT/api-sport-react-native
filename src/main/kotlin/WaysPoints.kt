package sport.models

import kotlinx.serialization.Serializable

@Serializable
data class WaypointsDTO(
    val latitude: Double,
    val longitude: Double
)