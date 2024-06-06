package xyz.potatoez.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.bson.BsonValue
import java.time.Clock


data class JWTConfig(
    val name: String,
    val realm: String,
    val secret: String,
    val audience: String,
    val issuer: String,
    val expirationSeconds: Long
)

fun JWTConfig.createToken(clock: Clock, accessToken: String, id: BsonValue?, expirationSeconds: Long): String =
    JWT.create()
        .withAudience(this.audience)
        .withIssuer(this.issuer)
        .withClaim("google_access_token", accessToken)
        .withClaim("user_id", id.toString())
        .withExpiresAt(clock.instant().plusSeconds(expirationSeconds))
        .sign(Algorithm.HMAC256(this.secret))