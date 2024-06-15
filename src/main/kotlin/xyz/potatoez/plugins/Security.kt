package xyz.potatoez.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import xyz.potatoez.model.JWTConfig

fun Application.configureSecurity(
    jwtConfig: JWTConfig
) {
    authentication {


        jwt(jwtConfig.name) {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtConfig.audience)) JWTPrincipal(credential.payload) else null
            }
            challenge { scheme, realm ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    mapOf("message" to "Token to access ${HttpHeaders.WWWAuthenticate} $scheme realm=$realm is either invalid or expired.")
                )
            }
        }

    }
}