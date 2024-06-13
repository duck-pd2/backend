package xyz.potatoez.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import xyz.potatoez.application.requests.EventRequest
import xyz.potatoez.application.requests.toDomain
import xyz.potatoez.domain.ports.EventRepository
import xyz.potatoez.domain.ports.UserRepository
import xyz.potatoez.utils.ParserICS

fun Route.eventRouting(repository: EventRepository, userRepository: UserRepository) {
    authenticate("auth-user") {
        post("/events") {
            val principal = call.principal<JWTPrincipal>() ?: run {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "missing JWT token"))
                return@post
            }
            val userId: ObjectId = principal.getClaim("user_id", ObjectId::class) ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "invalid credentials"))
                return@post
            }
            try {
                val user = userRepository.readUser(userId) ?: run {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "user not found"))
                    return@post
                }
                

            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
                return@post
            }

            try {
                val formData = call.receiveParameters()
                val url = formData["ics_url"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing ICS URL"))
                    return@post
                }

                // Parsing the ICS file to get the list of events
                val parsedEvents = ParserICS(url)

                // Storing each event into the database
                parsedEvents.forEach { eventMap ->
                    val eventRequest = EventRequest(eventMap)
                    val event = eventRequest.toDomain()
                    val id = repository.createEvent(event)

                    if (id == null) {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "Database error for event: ${event.title}"))
                        return@post
                    }
                }

                call.respond(HttpStatusCode.Created, mapOf("data" to parsedEvents))

            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            }
        }
    }

}
