package xyz.potatoez.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import xyz.potatoez.application.requests.EventRequest
import xyz.potatoez.application.requests.EventRequestWithID
import xyz.potatoez.application.requests.IcsRequest
import xyz.potatoez.application.requests.toDomain
import xyz.potatoez.domain.entity.SerializableEvent
import xyz.potatoez.domain.entity.User
import xyz.potatoez.domain.ports.EventRepository
import xyz.potatoez.domain.ports.UserRepository
import xyz.potatoez.model.JWTConfig
import xyz.potatoez.utils.parseICS

fun Route.eventRouting(repository: EventRepository, userRepository: UserRepository, jwtConfig: JWTConfig) {

    authenticate(jwtConfig.name) {
        post("/events") {
            try {
                val user = validateUser(userRepository) ?: return@post

                val url = call.receive<IcsRequest>().ics_url ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing ics url"))
                    return@post
                }

                // Parsing the ICS file to get the list of events
                val parsedEvents = parseICS(url)
                val listOfEvents = mutableListOf<ObjectId>()

                listOfEvents.addAll(user.events)
                // Storing each event into the database
                parsedEvents.forEach { eventMap ->
                    val event = EventRequest(eventMap).toDomain()
                    val id = repository.createEvent(event)

                    if (id == null) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("message" to "Database error for event: ${event.title}")
                        )
                        return@post
                    }
                    eventMap["id"] = id.asObjectId().value.toString()
                    listOfEvents.add(id.asObjectId().value)
                }
                val newUser = User(
                    id = user.id,
                    username = user.username,
                    pwd = user.pwd,
                    events = listOfEvents
                )
                userRepository.updateUser(user.id, newUser)
                call.respond(HttpStatusCode.Created, mapOf("data" to parsedEvents))

            } catch (e: CannotTransformContentToTypeException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing ics url"))
            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            }
        }
    }
    authenticate(jwtConfig.name) {
        post("/events/new") {
            try {
                val user = validateUser(userRepository) ?: return@post

                //make event from requests
                val event = call.receive<EventRequest>().toDomain()
                val id = repository.createEvent(event) ?: run {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error for event: ${event.title}")
                    )
                    return@post
                }

                // insert to user event list
                val eventList = mutableListOf(id.asObjectId().value)
                eventList.addAll(user.events)

                // construct new user
                val newUser = User(
                    id = user.id,
                    username = user.username,
                    pwd = user.pwd,
                    events = eventList
                )

                //update
                val success = userRepository.updateUser(user.id, newUser)
                if (success == 0L) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "insert event to user failed"))
                    return@post
                }

                call.respond(HttpStatusCode.Created, mapOf("data" to SerializableEvent(event)))

            } catch (e: CannotTransformContentToTypeException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing required parameters"))
            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            }
        }
    }
    authenticate(jwtConfig.name) {
        get("/events/{id?}") {
            try {

                val user = validateUser(userRepository) ?: return@get
                val id: String? = call.parameters["id"]
                if (id == null) {
                    val eventList = user.events.map {
                        repository.readEvent(it) ?: run {
                            return@get call.respond(HttpStatusCode.NotFound, mapOf("message" to "event not found"))
                        }
                    }
                    val serializedEventList = eventList.map { SerializableEvent(it) }
                    return@get call.respond(HttpStatusCode.OK, mapOf("data" to serializedEventList))
                }
                val event = repository.readEvent(ObjectId(id)) ?: run {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "event not found"))
                    return@get
                }
                call.respond(HttpStatusCode.OK, mapOf("data" to SerializableEvent(event)))
            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            }
        }
    }
    authenticate(jwtConfig.name) {
        put("/events/{id?}") {
            try {

                validateUser(userRepository) ?: return@put

                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing ID to update"))
                    return@put
                }

                repository.readEvent(ObjectId(id)) ?: run {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Event not found"))
                    return@put
                }

                val updateData = call.receive<EventRequestWithID>().toDomain()
                val success = repository.updateEvent(ObjectId(id), updateData)
                if (success == 0L) {
                    call.respond(HttpStatusCode.NotModified, mapOf("message" to "Event not updated"))
                    return@put
                } else {
                    call.respond(HttpStatusCode.Created, mapOf("data" to SerializableEvent(updateData)))
                }

            } catch (e: CannotTransformContentToTypeException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing required parameters"))
            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            }
        }
    }
    authenticate(jwtConfig.name) {
        delete("/events/{id?}") {
            try {

                val user = validateUser(userRepository) ?: return@delete

                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing id to delete"))
                    return@delete
                }

                val success = repository.deleteEvent(ObjectId(id))
                if (success == 0L) {
                    call.respond(HttpStatusCode.NotModified, mapOf("message" to "Event not deleted"))
                    return@delete
                } else {
                    val eventList = user.events.toMutableList()
                    eventList.removeAll{it == ObjectId(id) }
                    userRepository.updateUser(user.id, User(user.id, user.username, user.pwd, eventList))
                    call.respond(HttpStatusCode.NoContent, mapOf("message" to "Event delete success"))
                }

            } catch (e: Exception) {
                System.err.println(e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.localizedMessage))
            }
        }
    }
}

private suspend fun RoutingContext.validateUser(userRepository: UserRepository): User? {
    val principal = call.principal<JWTPrincipal>() ?: run {
        call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "missing JWT token"))
        return null
    }
    val userId: ObjectId = principal.getClaim("user_id", ObjectId::class) ?: run {
        call.respond(HttpStatusCode.BadRequest, mapOf("message" to "invalid credentials"))
        return null
    }

    val user = userRepository.readUser(userId) ?: run {
        call.respond(HttpStatusCode.NotFound, mapOf("message" to "user not found"))
        return null
    }
    return user
}
