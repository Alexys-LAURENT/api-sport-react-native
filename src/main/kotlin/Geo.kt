package sport.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Geo : Table("geo") {
    val idGeo = integer("id_geo").autoIncrement()
    val idTraining = integer("id_training").references(Trainings.idTraining)
    val date = timestamp("date")
    val localization = text("localization")

    override val primaryKey = PrimaryKey(idGeo)
}

@Serializable
data class GeoDTO(
    val idTraining: Int,
    val date: String,  // Comme une chaîne JSON
    val localization: String  // Comme une chaîne JSON
)