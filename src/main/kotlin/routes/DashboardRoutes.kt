package routes

import Services.DailyCaloriesResponse
import Services.DashBoardService
import Services.TrainingTypeCount
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class DashboardMonthlyResponse(
    val calories: Int,
    val trainings: List<TrainingTypeCount>,
    val dailyCalories: List<DailyCaloriesResponse>,
    val totalHours: Double
)

@Serializable
data class TrainingTypeCount(
    val type: String,
    val count: Int
)

@Serializable
data class DailyCaloriesResponse(
    val date: String,
    val calories: Int
)

@Serializable
data class MonthlyTrainingHoursResponse(
    val totalHours: Double
)

fun Application.configureDashboardRoutes() {
    val dashBoardService = DashBoardService()

    routing {
        route("/api/dashboard") {
            // Créer un point géographique

            // Récupérer un point géographique par ID
            get("{idUser}/{month}/{year}") {
                try {
                    val idUser = call.parameters["idUser"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID utilisateur invalide")
                        )

                    val month = call.parameters["month"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Mois invalide")
                        )

                    val year = call.parameters["year"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Année invalide")
                        )

                    // Récupérer les données des deux fonctions
                    val monthlyCalories = dashBoardService.getMonthlyCalories(idUser, month, year)
                    val monthlyTrainingTypes = dashBoardService.getTrainingTypesByMonth(idUser, month, year)
                    val dailyCalories = dashBoardService.getDailyCaloriesByMonth(idUser, month, year)
                    val totalHours = dashBoardService.getTotalTrainingHoursByMonth(idUser, month, year)

                    // Combiner les résultats dans une seule réponse
                    val dashboardResponse = DashboardMonthlyResponse(
                        calories = monthlyCalories.calories,
                        trainings = monthlyTrainingTypes.trainings,
                        dailyCalories = dailyCalories,
                        totalHours = totalHours.totalHours
                    )

                    // Répondre avec les données combinées
                    call.respond(HttpStatusCode.OK, dashboardResponse)

                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to (e.message ?: "Erreur serveur"))
                    )
                }
            }

        }
    }
}