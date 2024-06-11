package xyz.potatoez.routing

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.potatoez.application.requests.UserRequest
import xyz.potatoez.application.requests.toDomain
import xyz.potatoez.domain.ports.UserRepository
import xyz.potatoez.model.JWTConfig
import xyz.potatoez.model.createToken
import xyz.potatoez.utils.checkPwd
import xyz.potatoez.utils.hashPwd
import java.time.Clock

fun Route.userLogin(repository : UserRepository, jwtConfig: JWTConfig,  clock: Clock) {
    post("/register") {
        try {
            val userdata = call.receiveParameters()
            val username: String = userdata["username"] ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing username"))
                return@post
            }
            val pwd: String = userdata["pwd"] ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing password"))
                return@post
            }
            if (repository.readUser(username) != null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "AccountAlreadyExist"))
                return@post
            }

            val user = UserRequest(username, pwd).toDomain()
            val id = repository.createUser(user)

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "database error"))
                return@post
            }

            val objId = id.asObjectId().value
            val token = jwtConfig.createToken(clock, objId, 3600)
            call.respond(HttpStatusCode.Created, mapOf("token" to token))

        }catch (e: Exception) {
            System.err.println(e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            return@post
        }
    }
    post("login") {
        try {
            val userdata = call.receiveParameters()
            val username: String = userdata["username"] ?: run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Missing username"))
                return@post
            }
            val pwd: String = userdata["pwd"] ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing password"))
                return@post
            }
            val user = repository.readUser(username) ?: run {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "UserNotFound")
                )
                return@post
            }
            if (!checkPwd(user.pwd, pwd)) {
                System.err.println(user.pwd)
                System.err.println(hashPwd(pwd))
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "WrongPassword"))
                return@post
            }

            val id = user.id
            val token = jwtConfig.createToken(clock, id, 3600)

            call.respond(HttpStatusCode.Accepted, mapOf("token" to token))

        } catch (e: Exception) {
            System.err.println(e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            return@post
        }

    }
}