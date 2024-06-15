package xyz.potatoez.plugins

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.potatoez.domain.ports.EventRepository
import xyz.potatoez.domain.ports.UserRepository
import xyz.potatoez.infrastructure.repository.EventRepositoryImpl
import xyz.potatoez.infrastructure.repository.UserRepositoryImpl
import xyz.potatoez.model.JWTConfig
import xyz.potatoez.model.OAuthConfig
import xyz.potatoez.model.UserInfo
import xyz.potatoez.routing.eventRouting
import xyz.potatoez.routing.userLogin
import java.time.Clock

fun Application.configureRouting(
    jwtConfig: JWTConfig, database: MongoDatabase, clock: Clock
) {
    val userRepository: UserRepository = UserRepositoryImpl(database)
    val eventRepository: EventRepository = EventRepositoryImpl(database)
    val version = environment.config.property("ktor.api.version").getString()
    routing {
        get("/") {
            call.respondText("Hello world")
        }
        route("/api/v$version") {
            userLogin(userRepository, jwtConfig, clock)
            eventRouting(eventRepository, userRepository, jwtConfig)
        }
    }
}

private suspend fun getUserInfo(accessToken: String, oauthConfig: OAuthConfig, httpClient: HttpClient): UserInfo {
    return httpClient.get(oauthConfig.userInfoUrl) {
        headers {
            append("Authorization", "Bearer $accessToken")
        }
    }.body()
}