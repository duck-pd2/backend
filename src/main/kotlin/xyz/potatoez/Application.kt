package xyz.potatoez

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import xyz.potatoez.plugins.*
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
    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val clientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(environment.config.property("ktor.database.uri").getString()))
        .serverApi(serverApi)
        .build()
    val client = MongoClient.create(clientSettings)
    val database = client.getDatabase(environment.config.property("ktor.database.name").getString())
    println(database)

    configureSecurity(jwtConfig, googleConfig, httpClient)
//    configureHTTP()
//    configureSerialization()
//    configureDatabases()
    configureRouting(jwtConfig, googleConfig, httpClient, database, Clock.systemUTC())
    configureSwagger()
    configureHTTP()
    configureSerialization()
//    configureKoin()
}

fun Application.configureSwagger() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger-ui"
            forwardRoot = true
        }
        info {
            title = "Pd-Duck API"
            version = "1.0"
            description = "Pd-Duck API for NCKU application"
        }
    }
}



