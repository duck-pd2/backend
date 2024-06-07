package xyz.potatoez.application.requests

import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.User
import xyz.potatoez.model.UserInfo

data class UserRequest(
    val name: String,
    val refreshToken: String,
    val user: UserInfo
)

fun UserRequest.toDomain(): User = User(
    id = ObjectId(),
    name = name,
    pwd = refreshToken
)