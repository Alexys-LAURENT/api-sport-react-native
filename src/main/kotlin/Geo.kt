import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.LocalDateTime

object Geo : Table("geo") {
    val idGeo = integer("id_geo").autoIncrement()
    val idTraining = integer("id_training").references(Trainings.idTraining)
    val date = timestamp("date")
    val localization = text("localization")

    override val primaryKey = PrimaryKey(idGeo)
}

@Serializable
data class GeoPoint(
    val idGeo: Int,
    val idTraining: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val date: LocalDateTime,
    val localization: String
)