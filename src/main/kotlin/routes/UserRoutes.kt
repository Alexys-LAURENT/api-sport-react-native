package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import Services.UserService
import sport.models.UsersDTO
import sport.models.LoginDTO


fun Application.configureUserRoutes() {
    val userService = UserService()

    routing {
        // Route pour crée un utilisateur
         route("/inscription") {
             post {
                 try {
                     val dto = call.receive<UsersDTO>()
                     val created = userService.create(dto)
                     call.respond(HttpStatusCode.Created, created)

                 }catch (e: Exception) {
                     call.respond(
                         HttpStatusCode.BadRequest,
                         mapOf("error" to (e.message ?: "Erreur lors de la création"))
                     )
                 }

             }
         }

        // Route pour récupérer un utilisateur par ID
        get("/users/{id}") {
            try {
                val userId = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID invalide")
                    )

                val user = userService.getById(userId)  // Récupère l'utilisateur par ID
                if (user != null) {
                    call.respond(user)
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoie une erreur 404
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Utilisateur non trouvé")
                    )
                }
            } catch (e: Exception) {
                // En cas d'erreur (ex: erreur de base de données), renvoie une erreur 500
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to (e.message ?: "Erreur serveur"))
                )
            }

            route("/login") {
                post {
                    try {
                        val dto = call.receive<LoginDTO>() // Récupère les données envoyées (email, password)
                        val user = userService.login(dto.email, dto.hashedPass)

                        if (user != null) {
                            call.respond(HttpStatusCode.OK, mapOf("message" to "Connexion réussie", "user" to user))
                        } else {
                            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Email ou mot de passe incorrect"))
                        }
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to (e.message ?: "Erreur lors de la connexion"))
                        )
                    }
                }

                route ("/delete/{id}") {
                    delete {
                        val id = call.parameters["id"]?.toIntOrNull()

                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "ID invalide")
                        }


                    }

                }
            }


            route("/update/{id}") {
                put {
                    try {
                        val id = call.parameters["id"]?.toIntOrNull()

                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "ID invalide")
                            return@put
                        }

                        val dto = call.receive<UsersDTO>()
                        val isUpdated = userService.update(id, dto)

                        if (isUpdated) {
                            call.respond(HttpStatusCode.OK, "Utilisateur mis à jour avec succès")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Utilisateur non trouvé")
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Erreur interne du serveur : ${e.message}")
                    }
                }
            }


        }

    }
}
