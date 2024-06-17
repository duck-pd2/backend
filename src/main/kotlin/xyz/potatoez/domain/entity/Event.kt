package xyz.potatoez.domain.entity

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Event(
    @BsonId
    val id: ObjectId,
    val title: String,
    val start: String,
    val end: String,
    val description: String,
    val eventClass: String,
    val color: String,
    val tags: List<String>
)

@Serializable
data class SerializableEvent(
    val id: String,
    val title: String,
    val start: String,
    val end: String,
    val description: String,
    val eventClass: String,
    val color: String,
    val tags: List<String>
) {
    constructor(event: Event) : this(event.id.toString(), event.title, event.start, event.end, event.description, event.eventClass, event.color, event.tags)

}