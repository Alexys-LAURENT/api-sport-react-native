package Services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import sport.models.*

@Serializable
data class TrainingWithWaypoints(
    val trainingInfo: TrainingPageDTO,
    val waypoints: List<WaypointsDTO>
)

class TrainingPageService {
    // Récupérer les informations d'un entraînement avec ses waypoints
    suspend fun getTrainingInfosById(idTraining: Int): TrainingWithWaypoints? = transaction {
        // D'abord, récupérer les informations de l'entraînement
        val trainingInfo = TrainingPage.select { TrainingPage.idTraining eq idTraining }
            .map { row ->
                TrainingPageDTO(
                    idTraining = row[TrainingPage.idTraining],
                    idUser = row[TrainingPage.idUser],
                    idTrainingType = row[TrainingPage.idTrainingType],
                    startedDate = row[TrainingPage.startedDate].toString(),
                    endedDate = row[TrainingPage.endedDate]?.toString() ?: "",
                    calories = row[TrainingPage.calories],
                    difficulty = row[TrainingPage.difficulty],
                    feeling = row[TrainingPage.feeling],
                    distance = row[TrainingPage.distance]
                )
            }
            .singleOrNull() ?: return@transaction null

        // Ensuite, récupérer les waypoints associés à cet entraînement
        val waypoints = Geo.select { Geo.idTraining eq idTraining }
            .orderBy(Geo.date, SortOrder.ASC)
            .mapNotNull { row ->
                try {
                    // Parse du JSON pour extraire les coordonnées
                    val jsonString = row[Geo.localization]
                    val jsonElement = Json.parseToJsonElement(jsonString)

                    // Extraction des coordonnées latitude et longitude
                    val latitude = jsonElement.jsonObject["latitude"]?.jsonPrimitive?.doubleOrNull
                    val longitude = jsonElement.jsonObject["longitude"]?.jsonPrimitive?.doubleOrNull

                    if (latitude != null && longitude != null) {
                        WaypointsDTO(latitude = latitude, longitude = longitude)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null // Ignorer les entrées mal formatées
                }
            }

        // Retourner l'objet combiné
        TrainingWithWaypoints(
            trainingInfo = trainingInfo,
            waypoints = waypoints
        )
    }
}