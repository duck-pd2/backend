package xyz.potatoez.domain.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Event(
    @BsonId
    val id: ObjectId,
    val title: String,
    val start: String,
    val end: String,
    val description: String,
    val eventClass: String
)
