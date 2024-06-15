package xyz.potatoez.domain.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId,
    val username: String,
    val pwd: String,
    val events: List<ObjectId> = emptyList()
)