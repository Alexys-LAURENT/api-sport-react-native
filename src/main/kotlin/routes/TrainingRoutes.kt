package routes

import Services.TrainingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureTrainingRoutes() {
    val trainingService = TrainingService()

    routing {
        route("/api/trainings") {
            // Récupérer les entraînements d'un utilisateur avec une limite
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 5 // Par défaut 5

                val trainings = id?.let { trainingService.getAllTrainingsByUserId(it, limit) }

                if (trainings != null) {
                    call.respond(trainings)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entraînement non trouvé"))
                }
            }
        }
    }
}
