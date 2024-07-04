package io.sleekflow.application

import io.sleekflow.domain.Status
import io.sleekflow.domain.TodoTask
import io.sleekflow.infrastructure.database.repositories.TodoTaskRepository
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import io.sleekflow.infrastructure.network.proto.payload.CreateTodoTaskRequestPayload
import io.sleekflow.infrastructure.network.proto.payload.CreateTodoTaskResponsePayload
import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class CreateTodoTaskRequestHandler(private val todoTaskRepository: TodoTaskRepository): IRequestHandler {

    override suspend fun process(request: NetMessage): NetMessage {
        validateRequest(request)

        val payload = request.payload as CreateTodoTaskRequestPayload
        val newTodo = TodoTask(
            id = null,
            name = payload.name!!,
            description = payload.description!!,
            dueDate = Instant.fromEpochMilliseconds(payload.dueDate!!),
            status = Status.fromProto(payload.status!!)
        )

        val id = todoTaskRepository.insert(newTodo)

        return NetMessage(
            header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
            payload = CreateTodoTaskResponsePayload(ErrorCode.ErrorCode_Success, id)
        )
    }

    private fun validateRequest(request: NetMessage) {
        require(request.header !== null)
        require(request.header!!.messageType === MessageType.UserInitiatedRequest)
        require(request.header!!.requestType === RequestType.CreateTodoTask)
        require(request.payload is CreateTodoTaskRequestPayload)
    }
}