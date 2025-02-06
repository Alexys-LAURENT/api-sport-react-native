package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sport.models.GeoDTO
import Services.GeoService

// need to add this function call on Application.kt to make it work
fun Application.configureGeoRoutes() {
    val geoService = GeoService()

    routing {
        route("/api/geo") {
            // Créer un point géographique
            post {
                try {
                    val dto = call.receive<GeoDTO>()
                    val created = geoService.create(dto)
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to (e.message ?: "Erreur lors de la création"))
                    )
                }
            }

            // Récupérer un point géographique par ID
            get("{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID invalide")
                        )

                    val geoPoint = geoService.getById(id)
                    if (geoPoint != null) {
                        call.respond(geoPoint)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Point géographique non trouvé")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to (e.message ?: "Erreur serveur"))
                    )
                }
            }

            // Récupérer tous les points géographiques d'un entraînement
            get("training/{trainingId}") {
                try {
                    val trainingId = call.parameters["trainingId"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID d'entraînement invalide")
                        )

                    val points = geoService.getByTrainingId(trainingId)
                    call.respond(points)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to (e.message ?: "Erreur serveur"))
                    )
                }
            }

            // Mettre à jour un point géographique
            put("{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID invalide")
                        )

                    val dto = call.receive<GeoDTO>()
                    val updated = geoService.update(id, dto)

                    if (updated) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Point géographique non trouvé")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to (e.message ?: "Erreur lors de la mise à jour"))
                    )
                }
            }

            // Supprimer un point géographique
            delete("{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID invalide")
                        )

                    val deleted = geoService.delete(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "Point géographique non trouvé")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to (e.message ?: "Erreur lors de la suppression"))
                    )
                }
            }
        }
    }
}