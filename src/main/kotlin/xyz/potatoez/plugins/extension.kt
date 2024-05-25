package xyz.potatoez.plugins

import io.ktor.server.config.*
import xyz.potatoez.model.JWTConfig
import xyz.potatoez.model.OAuthConfig

fun ApplicationConfig.jwtConfig(): JWTConfig =
    JWTConfig(
        name = property("name").getString(),
        realm = property("realm").getString(),
        secret = property("secret").getString(),
        audience = property("audience").getString(),
        issuer = property("issuer").getString(),
        expirationSeconds = property("expirationSeconds").getString().toLong()
    )


fun ApplicationConfig.oauthConfig(): OAuthConfig =
    OAuthConfig(
        name = property("name").getString(),
        clientId = property("clientId").getString(),
        clientSecret = property("clientSecret").getString(),
        accessTokenUrl = property("accessTokenUrl").getString(),
        authorizeUrl = property("authorizeUrl").getString(),
        redirectUrl = property("redirectUrl").getString(),
        userInfoUrl = property("userInfoUrl").getString(),
        defaultScopes = property("defaultScopes").getList()
    )