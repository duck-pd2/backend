package xyz.potatoez

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.potatoez.plugins.configureDatabases
import xyz.potatoez.plugins.configureHTTP
import xyz.potatoez.plugins.configureSecurity

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        get("/hello") {
            call.respondText("Hello World!")
        }
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
