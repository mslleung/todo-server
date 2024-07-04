package io.sleekflow.infrastructure.network.proto.payload

import kotlinx.serialization.Serializable

@Serializable
class DeleteTodoTaskRequestPayload(
    val id: Int?
) : Payload()
