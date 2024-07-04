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
import io.sleekflow.infrastructure.network.proto.payload.UpdateTodoTaskRequestPayload
import io.sleekflow.infrastructure.network.proto.payload.UpdateTodoTaskResponsePayload
import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import io.sleekflow.infrastructure.network.proto.payload.messages.Status as ProtoStatus

class UpdateTodoTaskRequestHandlerTest {
    @Test
    fun `test update todo task`() = testApplication {
        val client = createWSClient()

        client.webSocket("/") {
            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.CreateTodoTask, UUID.randomUUID().toString()),
                    CreateTodoTaskRequestPayload(
                        generateRandomString(),
                        generateRandomString(),
                        Clock.System.now().toEpochMilliseconds(),
                        ProtoStatus.Status_NotStarted
                    )
                )
            )

            val createTodoTaskResponse = receiveDeserialized<NetMessage>()
            val createTodoTaskResponsePayload = createTodoTaskResponse.payload as CreateTodoTaskResponsePayload
            assertEquals(ErrorCode.ErrorCode_Success, createTodoTaskResponsePayload.errorCode)

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.UpdateTodoTask, UUID.randomUUID().toString()),
                    UpdateTodoTaskRequestPayload(
                        id = createTodoTaskResponsePayload.id,
                        name = generateRandomString(),
                        description = generateRandomString(),
                        Clock.System.now().toEpochMilliseconds(),
                        ProtoStatus.Status_InProgress
                    )
                )
            )
            val updateTodoTaskResponse = receiveDeserialized<NetMessage>()
            val updateTodoTaskResponsePayload = updateTodoTaskResponse.payload as UpdateTodoTaskResponsePayload
            assertEquals(ErrorCode.ErrorCode_Success, updateTodoTaskResponsePayload.errorCode)
        }
    }
}