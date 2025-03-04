package sport

import io.ktor.server.application.*
import routes.configureDashboardRoutes
import routes.configureGeoRoutes
import routes.configureTrainingRoutes
import routes.configureTrainingPageRoutes
import routes.configureUserRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureGeoRoutes()
    configureDashboardRoutes()
    configureTrainingRoutes()
    configureTrainingPageRoutes()
    configureUserRoutes()

}
