package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import Services.TrainingPageService

// need to add this function call on Application.kt to make it work
fun Application.configureTrainingPageRoutes() {
    val trainingPageService = TrainingPageService()

    routing {
        route("/api/trainingPage") {
            // Récupérer les informations d'un entraînement
            get("getTrainingInfoById/{id_training}") {
                try {
                    val idTraining = call.parameters["id_training"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID d'entraînement invalide")
                        )

                    val infosWithWaypoints = trainingPageService.getTrainingInfosById(idTraining)
                    if (infosWithWaypoints != null) {
                        call.respond(infosWithWaypoints)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Informations d'entraînement non trouvées")
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // Pour le débogage
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to (e.message ?: "Erreur serveur"))
                    )
                }
            }
        }
    }
}