package io.sleekflow

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
fun ApplicationTestBuilder.createWSClient(): HttpClient {
    return createClient {
        install(ContentNegotiation) {
            protobuf()
        }
        install(WebSockets) {
            contentConverter =
                KotlinxWebsocketSerializationConverter(ProtoBuf)
        }
    }
}

fun generateRandomString() = UUID.randomUUID().toString()
