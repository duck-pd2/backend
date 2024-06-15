package xyz.potatoez

import ch.qos.logback.classic.LoggerContext
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import org.slf4j.LoggerFactory
import xyz.potatoez.plugins.*
import java.time.Clock


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val jwtConfig = environment.config.config("ktor.auth.jwt").jwtConfig()
    val googleConfig = environment.config.config("ktor.auth.oauth.google").oauthConfig()

    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val clientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(environment.config.property("ktor.database.uri").getString()))
        .serverApi(serverApi)
        .build()
    val client = MongoClient.create(clientSettings)
    val database = client.getDatabase(environment.config.property("ktor.database.name").getString())

    val loggerContent = LoggerFactory.getILoggerFactory() as LoggerContext
    loggerContent.getLogger("org.mongodb.driver").level = ch.qos.logback.classic.Level.OFF

    configureSecurity(jwtConfig)
    configureRouting(jwtConfig, database, Clock.systemUTC())
    configureHTTP()
    configureSerialization()

}





