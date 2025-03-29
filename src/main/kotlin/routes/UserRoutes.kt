package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import Services.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import sport.models.*
import java.util.Date


fun Application.configureUserRoutes() {
    val userService = UserService()

    routing {
        // Route pour crée un utilisateur
        route("/inscription") {
            post {
                try {
                    val dto = call.receive<SignInDTO>()
                    val created = userService.create(dto)
                    val token = JWT.create()
                        .withClaim("email", created.email)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256("secretToken"))
                    val response = SignInResponseDTO(
                        message = "Inscription réussie",
                        token = token
                    )
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
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

                val user = userService.getById(userId)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Utilisateur non trouvé")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to (e.message ?: "Erreur serveur"))
                )
            }
        }

        route("/login") {
            post {
                try {
                    val log = call.receive<LoginDTO>()
                    val user = userService.login(log.email, log.hashedPass)
                    println("Requête reçue: email=${log.email}, hashedPass=${log.hashedPass}")

                    if (user != null) {
                        println("user != null")
                        val token = JWT.create()
                            .withClaim("email", user.email)
                            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                            .sign(Algorithm.HMAC256("secretToken"))

                        val response = user.copy(token = token)
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Email ou mot de passe incorrect"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Erreur inconnue")))
                }
            }
        }

        route("/delete/{id}") {
            delete {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID invalide")
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
