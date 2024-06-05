package xyz.potatoez.domain.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import xyz.potatoez.application.responses.UserResponse
import xyz.potatoez.model.UserInfo

data class User (
    @BsonId
    val id: ObjectId,
    val name: String,
    val refreshToken: String,
    val user: UserInfo
) {
    fun toResponse() = UserResponse(
        id = id.toString(),
        name = name,
        refreshToken = refreshToken,
        user = user
    )
}