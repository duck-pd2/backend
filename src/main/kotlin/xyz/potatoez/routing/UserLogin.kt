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
import java.time.Clock

fun Route.userLogin(repository : UserRepository, jwtConfig: JWTConfig,  clock: Clock) {
    post("/register") {
        try {
            val userdata = call.receiveParameters()
            val user = UserRequest(userdata["username"]!!, userdata["pwd"]!!).toDomain()
            val id = repository.createUser(user)
            val token = jwtConfig.createToken(clock, id, 3600)
            call.respond(HttpStatusCode.Created, mapOf("token" to token))
        }catch (e: Exception) {
            System.err.println(e)
        }
    }
    post("login") {
        val userdata = call.receive<UserRequest>().toDomain()
        val user = repository.readUser(userdata.username) ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "UserNotFound"))
        if (!checkPwd(user.pwd, userdata.pwd)) {
            return@post call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "WrongPassword"))
        }
//        val token = jwtConfig.createToken(clock, , 3600)
    }
}