package xyz.potatoez.application.requests

import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.User
import xyz.potatoez.utils.hashPwd

data class UserRequest(
    val username: String,
    val pwd: String
)

fun UserRequest.toDomain(): User = User(
    id = ObjectId(),
    username = username,
    pwd = hashPwd(pwd)
)
