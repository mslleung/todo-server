package io.sleekflow.application

import io.sleekflow.infrastructure.network.proto.NetMessage

interface IRequestHandler {
    suspend fun process(request: NetMessage): NetMessage
}