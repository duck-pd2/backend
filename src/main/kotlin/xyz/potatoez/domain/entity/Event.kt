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
    val userId: ObjectId?
)

@Serializable
data class SerializableEvent(
    val title: String,
    val start: String,
    val end: String,
    val description: String,
    val eventClass: String,
) {
    constructor(event: Event) : this(event.title, event.start, event.start, event.end, event.eventClass)

}