package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sport.models.TrainingDTO
import Services.TrainingService

fun Application.configureTrainingRoutes() {
    val TrainingService = TrainingService()

    routing {
        route("/api/trainings") {
            // Récupérer un entraînement par ID
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val training = id?.let { TrainingService.getAllTrainingByIdUser(it) }
                if (training != null) {
                    call.respond(training)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entraînement non trouvé"))
                }
            }
        }
    }
}
