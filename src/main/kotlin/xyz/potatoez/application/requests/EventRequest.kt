package xyz.potatoez.application.requests

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.Event

@Serializable
data class EventRequestWithID(
    val id: String,
    val title: String,
    val start: String,
    val end: String,
    val description: String,
    val eventClass: String,
    val color: String,
    val tags: List<String>,
)


fun EventRequestWithID.toDomain(): Event = Event(
    id = ObjectId(id),
    title = title,
    start = start,
    end = end,
    description = description,
    eventClass = eventClass,
    color = color,
    tags = tags
)

data class EventRequest(
    val title: String,
    val start: String,
    val end: String,
    val description: String,
    val eventClass: String,
    val color: String,
    val tags: List<String>,
) {
    constructor(contentMap: Map<String, String>): this(title = contentMap["title"].toString(),
        start = contentMap["start"].toString(), end = contentMap["end"].toString(),
        description = contentMap["description"].toString(),
        eventClass = contentMap["color"].toString(), color = contentMap["color"].toString(), tags = listOf()
    )
}

fun EventRequest.toDomain(): Event = Event(
    id = ObjectId(),
    title = title,
    start = start,
    end = end,
    description = description,
    eventClass = eventClass,
    color = color,
    tags = tags
)

@Serializable
data class IcsRequest(val ics_url: String? = null)