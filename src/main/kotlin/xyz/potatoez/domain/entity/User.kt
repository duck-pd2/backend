package xyz.potatoez.domain.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import xyz.potatoez.application.responses.UserResponse

data class User (
    @BsonId
    val id: ObjectId,
    val username: String,
    val pwd: String
) {
    fun toResponse() = UserResponse(
        id = id.toString(),
        name = username,
        pwd = pwd
    )
}