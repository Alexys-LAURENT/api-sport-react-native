package Services
import TrainingTypes
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import sport.models.Geo
import sport.models.GeoDTO
import sport.models.Trainings
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.round

@Serializable
data class MonthlyCaloriesResponse(
    val calories: Int
)

@Serializable
data class MonthlyTrainingHoursResponse(
    val totalHours: Double
)

@Serializable
data class TrainingTypeCount(
    val type: String,
    val count: Int,
    val color: String
)

@Serializable
data class MonthlyTrainingTypesResponse(
    val trainings: List<TrainingTypeCount>
)

@Serializable
data class DailyCaloriesResponse(
    val date: String,
    val calories: Int
)

class DashBoardService {
    // Créer un point géographique
    suspend fun getMonthlyCalories(idUser: Int, month: Int, year: Int): MonthlyCaloriesResponse = transaction {
        // Formatage du mois avec deux chiffres
        val formattedMonth = month.toString().padStart(2, '0')
        val yearMonth = "$year-$formattedMonth"

        // Récupération et calcul des calories
        val totalCalories = Trainings
            .select { Trainings.idUser eq idUser }
            .mapNotNull { row ->
                val startDate = row[Trainings.startedDate].toString()
                val calories = row[Trainings.calories]

                // On ne prend que les entraînements du mois demandé
                if (startDate.substring(0, 7) == yearMonth) {
                    calories ?: 0
                } else {
                    null
                }
            }
            .sum()

        // Retour du résultat dans le format demandé
        MonthlyCaloriesResponse(calories = totalCalories)
    }

    /**
     * Récupère le nombre d'entraînements par type pour un utilisateur durant un mois spécifique
     *
     * @param idUser L'identifiant de l'utilisateur
     * @param month Le numéro du mois (1-12)
     * @param year L'année
     * @return Une liste des types d'entraînements avec leur nombre d'occurrences
     */
    suspend fun getTrainingTypesByMonth(idUser: Int, month: Int, year: Int): MonthlyTrainingTypesResponse = transaction {
        // Récupérer les données avec le champ color
        val trainings = (Trainings innerJoin TrainingTypes)
            .slice(TrainingTypes.label, Trainings.startedDate, TrainingTypes.color)
            .select { Trainings.idUser eq idUser }
            .map { row ->
                val timestamp = row[Trainings.startedDate]
                val dateString = timestamp.toString()
                val typeName = row[TrainingTypes.label]
                val color = row[TrainingTypes.color]

                Triple(typeName, dateString, color)
            }

        // Formatage du mois avec deux chiffres
        val formattedMonth = month.toString().padStart(2, '0')
        val yearMonth = "$year-$formattedMonth"

        // Filtrez et comptez en mémoire
        val results = trainings
            .filter { (_, dateString, _) -> dateString.substring(0, 7) == yearMonth }
            .groupBy { it.first }  // Grouper par nom de type
            .map { (type, list) ->
                // Prendre la première couleur de la liste, supposant que toutes les couleurs sont identiques pour un type donné
                val color = list.first().third
                TrainingTypeCount(type = type, count = list.size, color = color)
            }

        MonthlyTrainingTypesResponse(trainings = results)
    }

    /**
     * Récupère les calories dépensées par jour pour un utilisateur durant un mois spécifique
     *
     * @param idUser L'identifiant de l'utilisateur
     * @param month Le numéro du mois (1-12)
     * @param year L'année
     * @return Une liste de DailyCaloriesResponse contenant la date et les calories pour chaque jour du mois
     */
    suspend fun getDailyCaloriesByMonth(idUser: Int, month: Int, year: Int): List<DailyCaloriesResponse> = transaction {
        // Créer un objet YearMonth pour gérer correctement les jours dans le mois
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = yearMonth.lengthOfMonth()

        // Créer un map pour stocker les calories par jour (initialisé à 0 pour tous les jours)
        val dailyCaloriesMap = (1..daysInMonth).associate { day ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month, day)
            formattedDate to 0
        }.toMutableMap()

        // Formatage du mois avec deux chiffres pour la comparaison
        val formattedMonth = month.toString().padStart(2, '0')
        val yearMonthStr = "$year-$formattedMonth"

        // Récupération des entraînements du mois demandé
        val trainings = Trainings
            .select { Trainings.idUser eq idUser }
            .mapNotNull { row ->
                val startDateTimestamp = row[Trainings.startedDate]
                val startDateStr = startDateTimestamp.toString()
                val calories = row[Trainings.calories] ?: 0

                // Vérifier si l'entraînement appartient au mois demandé
                if (startDateStr.substring(0, 7) == yearMonthStr) {
                    // Extraire seulement YYYY-MM-DD, peu importe le format d'origine
                    // Cette approche fonctionnera avec les deux formats que tu as reçus
                    val dateOnly = startDateStr.split("T")[0]

                    Pair(dateOnly, calories)
                } else {
                    null
                }
            }

        // Agréger les calories par jour
        trainings.forEach { (date, calories) ->
            dailyCaloriesMap[date] = (dailyCaloriesMap[date] ?: 0) + calories
        }

        // Transformer le map en liste de DailyCaloriesResponse
        dailyCaloriesMap.entries
            .sortedBy { it.key }
            .map { (date, calories) ->
                DailyCaloriesResponse(date = date, calories = calories)
            }
    }

    suspend fun getTotalTrainingHoursByMonth(idUser: Int, month: Int, year: Int): MonthlyTrainingHoursResponse = transaction {
        // Formatage du mois avec deux chiffres
        val formattedMonth = month.toString().padStart(2, '0')
        val yearMonth = "$year-$formattedMonth"

        // Récupération des entraînements du mois demandé
        val totalHours = Trainings
            .select { Trainings.idUser eq idUser }
            .mapNotNull { row ->
                val startDate = row[Trainings.startedDate]
                val endDate = row[Trainings.endedDate] ?: return@mapNotNull null

                // Vérifier si l'entraînement appartient au mois demandé
                val startDateStr = startDate.toString()
                if (startDateStr.substring(0, 7) == yearMonth) {
                    // Calculer la durée en heures
                    val duration = endDate.toEpochMilli() - startDate.toEpochMilli()
                    duration.toDouble() / (1000 * 60 * 60)
                } else {
                    null
                }
            }
            .sum()

        // Arrondir le total d'heures à deux décimales
        val roundedTotalHours = round(totalHours * 100) / 100

        // Retour du résultat dans le format demandé
        MonthlyTrainingHoursResponse(totalHours = roundedTotalHours)
    }
}