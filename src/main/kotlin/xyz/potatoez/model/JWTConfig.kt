package xyz.potatoez.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.bson.types.ObjectId
import java.time.Clock


data class JWTConfig(
    val name: String,
    val realm: String,
    val secret: String,
    val audience: String,
    val issuer: String,
    val expirationSeconds: Long
)

fun JWTConfig.createToken(clock: Clock, id: ObjectId, username: String, expirationSeconds: Long): String =
    JWT.create()
        .withAudience(this.audience)
        .withIssuer(this.issuer)
        .withClaim("user_id", id.toString())
        .withClaim("username", username)
        .withExpiresAt(clock.instant().plusSeconds(expirationSeconds))
        .sign(Algorithm.HMAC256(this.secret))