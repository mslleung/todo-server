package io.sleekflow.application

import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.sleekflow.createWSClient
import io.sleekflow.domain.Status
import io.sleekflow.domain.TodoTask
import io.sleekflow.generateRandomString
import io.sleekflow.infrastructure.network.proto.Header
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import io.sleekflow.infrastructure.network.proto.payload.*
import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import io.sleekflow.infrastructure.network.proto.payload.messages.Status as ProtoStatus

class GetTodoTaskRequestHandlerTest {

    @Test
    fun `test get todo task`() = testApplication {
        val client = createWSClient()

        client.webSocket("/") {
            val expectedTodoTask = TodoTask(
                null,
                generateRandomString(),
                generateRandomString(),
                Clock.System.now(),
                Status.NotStarted
            )

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.CreateTodoTask, UUID.randomUUID().toString()),
                    CreateTodoTaskRequestPayload(
                        expectedTodoTask.name,
                        expectedTodoTask.description,
                        expectedTodoTask.dueDate.toEpochMilliseconds(),
                        expectedTodoTask.status.toProto()
                    )
                )
            )

            val createTodoTaskResponse = receiveDeserialized<NetMessage>()
            val createTodoTaskResponsePayload = createTodoTaskResponse.payload as CreateTodoTaskResponsePayload
            assertEquals(ErrorCode.ErrorCode_Success, createTodoTaskResponsePayload.errorCode)

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.GetTodoTask, UUID.randomUUID().toString()),
                    GetTodoTaskRequestPayload(null, null)
                )
            )
            val getTodoTaskResponse = receiveDeserialized<NetMessage>()
            val getTodoTaskResponsePayload = getTodoTaskResponse.payload as GetTodoTaskResponsePayload
            assertEquals(1, getTodoTaskResponsePayload.todos.size)
            assertEquals(expectedTodoTask.name, getTodoTaskResponsePayload.todos[0].name)
            assertEquals(expectedTodoTask.description, getTodoTaskResponsePayload.todos[0].description)
            assertEquals(
                expectedTodoTask.dueDate.toEpochMilliseconds(),
                getTodoTaskResponsePayload.todos[0].dueDate
            )
            assertEquals(expectedTodoTask.status.toProto(), getTodoTaskResponsePayload.todos[0].status)
        }
    }

    @Test
    fun `test get todo task filtered`() = testApplication {
        val client = createWSClient()

        client.webSocket("/") {
            val expectedTodoTask = TodoTask(
                null,
                generateRandomString(),
                generateRandomString(),
                Clock.System.now(),
                Status.NotStarted
            )

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.CreateTodoTask, UUID.randomUUID().toString()),
                    CreateTodoTaskRequestPayload(
                        expectedTodoTask.name,
                        expectedTodoTask.description,
                        expectedTodoTask.dueDate.toEpochMilliseconds(),
                        expectedTodoTask.status.toProto()
                    )
                )
            )

            val createTodoTaskResponse = receiveDeserialized<NetMessage>()
            val createTodoTaskResponsePayload = createTodoTaskResponse.payload as CreateTodoTaskResponsePayload
            assertEquals(ErrorCode.ErrorCode_Success, createTodoTaskResponsePayload.errorCode)

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.GetTodoTask, UUID.randomUUID().toString()),
                    GetTodoTaskRequestPayload(
                        GetTodoTaskFilter(
                            dueDateRangeStart = null,
                            dueDateRangeEnd = null,
                            statusList = listOf(ProtoStatus.Status_InProgress)
                        ),
                        null
                    )
                )
            )
            val getTodoTaskResponse = receiveDeserialized<NetMessage>()
            val getTodoTaskResponsePayload = getTodoTaskResponse.payload as GetTodoTaskResponsePayload
            assertEquals(0, getTodoTaskResponsePayload.todos.size)
        }
    }

    @Test
    fun `test get todo task sorted`() = testApplication {
        val client = createWSClient()

        client.webSocket("/") {
            val expectedTodoTask1 = TodoTask(
                null,
                "2",
                generateRandomString(),
                Clock.System.now(),
                Status.NotStarted
            )
            val expectedTodoTask2 = TodoTask(
                null,
                "1",
                generateRandomString(),
                Clock.System.now(),
                Status.NotStarted
            )

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.CreateTodoTask, UUID.randomUUID().toString()),
                    CreateTodoTaskRequestPayload(
                        expectedTodoTask1.name,
                        expectedTodoTask1.description,
                        expectedTodoTask1.dueDate.toEpochMilliseconds(),
                        expectedTodoTask1.status.toProto()
                    )
                )
            )
            receiveDeserialized<NetMessage>()

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.CreateTodoTask, UUID.randomUUID().toString()),
                    CreateTodoTaskRequestPayload(
                        expectedTodoTask2.name,
                        expectedTodoTask2.description,
                        expectedTodoTask2.dueDate.toEpochMilliseconds(),
                        expectedTodoTask2.status.toProto()
                    )
                )
            )
            receiveDeserialized<NetMessage>()

            sendSerialized(
                NetMessage(
                    Header(MessageType.UserInitiatedRequest, RequestType.GetTodoTask, UUID.randomUUID().toString()),
                    GetTodoTaskRequestPayload(
                        null,
                        GetTodoTaskSort(
                            sortBy = GetTodoTaskSortBy.GetTodoTaskSortBy_ByName,
                            sortOrder = GetTodoTaskSortOrder.GetTodoTaskSortOrder_ASC
                        )
                    )
                )
            )
            val getTodoTaskResponse = receiveDeserialized<NetMessage>()
            val getTodoTaskResponsePayload = getTodoTaskResponse.payload as GetTodoTaskResponsePayload
            assertEquals(2, getTodoTaskResponsePayload.todos.size)
            assertEquals("1", getTodoTaskResponsePayload.todos[0].name)
            assertEquals("2", getTodoTaskResponsePayload.todos[1].name)
        }
    }
}
