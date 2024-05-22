package xyz.potatoez.callAPI

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

object callAPI {
    private const val BASE_URL = "https://api.wavjaby.nckuctf.org/api/v0/"
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchCourse(path: String, params: Map<String, String>): CourseSearchResponse {
        httpClient.use { client ->
            val res = client.get(BASE_URL + path) {
                url {
                    params.forEach { (key, value) -> parameters.append(key, value) }
                }
            }
            val body = res.body<CourseSearchResponse>()
            return body
        }
    }
}

