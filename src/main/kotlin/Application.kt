package sport

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.configureDashboardRoutes
import routes.configureGeoRoutes
import routes.configureTrainingRoutes
import routes.configureTrainingPageRoutes
import routes.configureUserRoutes
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.response.*
import java.util.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.configureSecurity() {
    val secretKey = "RhaegarIsTheBestTargaryen"
    val issuer = "http://127.0.0.1:8083"
    val audience = "http://127.0.0.1:8083/hello"
    val myRealm = "Access to 'hello'"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secretKey))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}

fun Application.module() {
    try {
        // Initialiser la base de données
        DatabaseFactory.init()

        // Configurer la sérialisation
        configureSerialization()

        // Route de test pour vérifier si l'application fonctionne
        routing {
            get("/") {
                call.respondText("FitTrack API is running!", ContentType.Text.Plain)
            }
        }

        // Configurer les routes
        configureGeoRoutes()
        configureDashboardRoutes()
        configureTrainingRoutes()
        configureTrainingPageRoutes()
        configureUserRoutes()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
