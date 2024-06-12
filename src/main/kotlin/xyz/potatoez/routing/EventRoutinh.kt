package xyz.potatoez.routing

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import xyz.potatoez.application.requests.EventRequest
import xyz.potatoez.application.requests.toDomain
import xyz.potatoez.domain.ports.EventRepository
import xyz.potatoez.utils.ParserICS

fun Route.eventRouting(repository: EventRepository) {
    post("/register") {
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
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Database error for event: ${event.title}"))
                    return@post
                }
            }

            call.respond(HttpStatusCode.Created, mapOf("message" to "Events registered successfully"))

        } catch (e: Exception) {
            System.err.println(e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
        }
    }
}
