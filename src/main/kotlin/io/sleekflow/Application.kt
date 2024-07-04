package io.sleekflow

import io.ktor.server.application.*
import io.sleekflow.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSockets()
    configureDependencyInjection()
}
