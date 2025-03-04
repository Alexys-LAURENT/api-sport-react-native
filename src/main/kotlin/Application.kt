package sport

import io.ktor.server.application.*
import routes.configureGeoRoutes
import routes.configureUserRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureGeoRoutes()
    configureUserRoutes()

}
