package routes

import Services.TrainingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import sport.models.TrainingDTO

// Ajout de la data class pour le corps de la requête
@Serializable
data class UpdateTrainingRequest(
    val difficulty: String,
    val feeling: String? = null
)

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

            // Mettre à jour un entraînement par ID
            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID invalide"))
                    return@put
                }

                val updateRequest = call.receive<UpdateTrainingRequest>()
                System.out.println(updateRequest)
                if (updateRequest == null || updateRequest.difficulty.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Données invalides"))
                    return@put
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