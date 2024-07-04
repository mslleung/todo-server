package io.sleekflow.application

import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.sleekflow.createWSClient
import io.sleekflow.generateRandomString
import io.sleekflow.infrastructure.network.proto.Header
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import io.sleekflow.infrastructure.network.proto.payload.CreateTodoTaskRequestPayload
import io.sleekflow.infrastructure.network.proto.payload.CreateTodoTaskResponsePayload
import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import io.sleekflow.infrastructure.network.proto.payload.messages.Status as ProtoStatus

class CreateTodoTaskRequestHandlerTest {

    @Test
    fun `test create todo task`() = testApplication {
        val client = createWSClient()

        client.webSocket("/") {
            val requestId = UUID.randomUUID().toString()
            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.CreateTodoTask, requestId),
                    CreateTodoTaskRequestPayload(
                        generateRandomString(),
                        generateRandomString(),
                        Clock.System.now().toEpochMilliseconds(),
                        ProtoStatus.Status_NotStarted
                    )
                )
            )

            val response = receiveDeserialized<NetMessage>()
            assertEquals(requestId, response.header!!.requestUuid)
            assertEquals(ErrorCode.ErrorCode_Success, (response.payload as CreateTodoTaskResponsePayload).errorCode)
        }
    }
}