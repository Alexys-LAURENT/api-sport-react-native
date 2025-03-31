package routes

import Services.TrainingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import sport.models.TrainingDTO
import io.ktor.server.request.*

// Data class for creating a new training
@Serializable
data class CreateTrainingRequest(
    val idUser: Int,
    val idTrainingType: Int
)

// Data class for updating a training
@Serializable
data class UpdateTrainingRequest(
    val difficulty: String,
    val calories: Int,
    val feeling: String? = null
)

fun Application.configureTrainingRoutes() {
    val trainingService = TrainingService()

    routing {
        route("/api/trainings") {
            post("/create") {
                try {
                    val createRequest = call.receive<CreateTrainingRequest>()

                    // Log received data
                    println("Received training creation request:")
                    println("User ID: ${createRequest.idUser}")
                    println("Training Type ID: ${createRequest.idTrainingType}")

                    // Valider les données d'entrée
                    if (createRequest.idUser <= 0 || createRequest.idTrainingType <= 0) {
                        println("Invalid input data")
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Données invalides"))
                        return@post
                    }

                    // Créer l'entraînement via le service
                    val newTrainingId = trainingService.createTraining(
                        createRequest.idUser,
                        createRequest.idTrainingType
                    )

                    // Répondre avec l'ID du nouvel entraînement
                    call.respond(HttpStatusCode.Created, mapOf("id" to newTrainingId))
                } catch (e: Exception) {
                    // Log full stack trace
                    e.printStackTrace()

                    println("Detailed error message: ${e.message}")
                    println("Error cause: ${e.cause}")

                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Erreur de création de l'entraînement", "details" to (e.message ?: "Unknown error"))
                    )
                }
            }


            // Récupérer les entraînements d'un utilisateur avec une limite
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val trainings = id?.let {
                    if (limit != null) {
                        trainingService.getAllTrainingsByUserId(it, limit)
                    } else {
                        trainingService.getAllTrainingsByUserId(it)
                    }
                }

                if (trainings != null) {
                    call.respond(trainings)
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

                if (updateRequest.difficulty.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Données invalides"))
                    return@put
                }

                val updated = trainingService.updateTraining(id, updateRequest.difficulty, updateRequest.calories, updateRequest.feeling)
                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Entraînement mis à jour"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entraînement non trouvé"))
                }
            }
        }
    }
}