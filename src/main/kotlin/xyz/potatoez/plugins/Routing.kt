package xyz.potatoez.plugins

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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
    jwtConfig: JWTConfig, oauthConfig: OAuthConfig,
    httpClient: HttpClient, database: MongoDatabase, clock: Clock
) {
    val userRepository: UserRepository = UserRepositoryImpl(database)
    val eventRepository: EventRepository = EventRepositoryImpl(database)
    val version = environment.config.property("ktor.api.version").getString()
    routing {
        get("/") {
            call.respondText("Hello world")
        }
        get("/home") {
            val principal = call.principal<JWTPrincipal>() ?: run {
                call.respondText("Not logged in")
                return@get
            }
            call.respondText("Hello, ${principal}!")
        }

        authenticate(jwtConfig.name) {
            get("/me") {
                val principal = call.principal<JWTPrincipal>() ?: run {
                    call.respond(HttpStatusCode.Forbidden, "Not logged in")
                    return@get
                }
                val accessToken = principal.getClaim("google_access_token", String::class) ?: run {
                    call.respond(HttpStatusCode.Forbidden, "No access token")
                    return@get
                }
                val userInfo = getUserInfo(accessToken, oauthConfig, httpClient)
                call.respondText("Hi, ${userInfo.name}!")
            }
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