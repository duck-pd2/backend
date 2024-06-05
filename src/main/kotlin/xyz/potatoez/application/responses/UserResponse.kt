package xyz.potatoez.application.responses

import xyz.potatoez.model.UserInfo

data class UserResponse(
    val id: String,
    val name: String,
    val refreshToken: String,
    val user: UserInfo
)