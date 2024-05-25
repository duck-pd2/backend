package xyz.potatoez

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.potatoez.plugins.configureRouting
import xyz.potatoez.plugins.configureSecurity
import xyz.potatoez.plugins.jwtConfig
import xyz.potatoez.plugins.oauthConfig
import java.time.Clock


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val jwtConfig = environment.config.config("ktor.auth.jwt").jwtConfig()
    val googleConfig = environment.config.config("ktor.auth.oauth.google").oauthConfig()
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    configureSecurity(jwtConfig, googleConfig, httpClient)
//    configureHTTP()
//    configureSerialization()
//    configureDatabases()
    configureRouting(jwtConfig, googleConfig, httpClient, Clock.systemUTC())
}

fun Application.configureRouting() {
    routing {
        get("/hello") {
            call.respondText("Hello World!")
        }
    }
}
