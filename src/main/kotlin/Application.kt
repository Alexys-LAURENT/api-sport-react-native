package sport

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.configureDashboardRoutes
import routes.configureGeoRoutes
import routes.configureTrainingRoutes
import routes.configureTrainingPageRoutes
import routes.configureUserRoutes
import io.ktor.http.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
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
