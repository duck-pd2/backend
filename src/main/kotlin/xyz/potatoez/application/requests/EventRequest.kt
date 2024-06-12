package xyz.potatoez.application.requests

import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.Event
import xyz.potatoez.domain.entity.User
import xyz.potatoez.utils.hashPwd

data class EventRequest(val contentMap: Map<String, String>) {
    val title by contentMap
    val start by contentMap
    val end by contentMap
    val description by contentMap
}

fun EventRequest.toDomain(): Event = Event(
    id = ObjectId(),
    title = title,
    start = start,
    end = end,
    description =description
)
