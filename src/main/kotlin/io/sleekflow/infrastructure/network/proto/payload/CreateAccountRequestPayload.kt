package io.sleekflow.infrastructure.network.proto.payload

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequestPayload(
    val username: String?,
    val password: String?
) : Payload()
