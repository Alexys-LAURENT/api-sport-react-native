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
            // Créer un point géographique
            // Créer un point géographique
            post {
                try {
                    println("🔵 [GeoRoutes] POST /api/geo - Début de la requête")

                    // Log de l'en-tête de la requête
                    println("🔵 [GeoRoutes] Headers: ${call.request.headers.entries().joinToString { "${it.key}: ${it.value}" }}")

                    // Lire le corps une seule fois et le convertir en DTO
                    val dto = call.receive<GeoDTO>()

                    // Log du DTO après désérialisation
                    println("🔵 [GeoRoutes] DTO reçu: $dto")

                    // Log avant l'appel du service
                    println("🔵 [GeoRoutes] Appel du service geoService.create()")
                    val created = geoService.create(dto)

                    // Log du résultat du service
                    println("🔵 [GeoRoutes] Résultat du service: $created")

                    // Réponse au client
                    call.respond(HttpStatusCode.Created, created)
                    println("🔵 [GeoRoutes] POST /api/geo - Réponse envoyée avec succès: ${HttpStatusCode.Created}")
                } catch (e: Exception) {
                    // Log détaillé de l'erreur
                    println("🔴 [GeoRoutes] POST /api/geo - Erreur: ${e.message}")
                    println("🔴 [GeoRoutes] Stacktrace: ${e.stackTraceToString()}")

                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to (e.message ?: "Erreur lors de la création"))
                    )
                    println("🔴 [GeoRoutes] POST /api/geo - Réponse d'erreur envoyée: ${HttpStatusCode.BadRequest}")
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