package routes

import Services.TrainingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sport.models.TrainingDTO

fun Application.configureTrainingRoutes() {
    val TrainingService = TrainingService()

    routing {
        route("/api/trainings") {
            // Récupérer un entraînement par ID
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val training = id?.let { TrainingService.getAllTrainingsByUserId(id) }
                if (training != null) {
                    call.respond(training)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entraînement non trouvé"))
                }
            }
            post("{id}/update") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))
                    return@post
                }

                val updateRequest = call.receiveOrNull<UpdateTrainingRequest>()
                if (updateRequest == null || updateRequest.difficulty.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Données invalides"))
                    return@post
                }

                val updated = TrainingService.updateTraining(id, updateRequest.difficulty, updateRequest.feeling)
                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Entraînement mis à jour"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entraînement non trouvé"))
                }
            }

        }
    }
}
