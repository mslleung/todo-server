package io.sleekflow.application

import io.sleekflow.domain.Status
import io.sleekflow.domain.TodoTask
import io.sleekflow.infrastructure.database.repositories.TodoTaskRepository
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import io.sleekflow.infrastructure.network.proto.payload.UpdateTodoTaskRequestPayload
import io.sleekflow.infrastructure.network.proto.payload.UpdateTodoTaskResponsePayload
import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.datetime.Instant

class UpdateTodoTaskRequestHandler(private val todoTaskRepository: TodoTaskRepository) : IRequestHandler {

    override suspend fun process(request: NetMessage): NetMessage {
        validateRequest(request)

        val payload = request.payload as UpdateTodoTaskRequestPayload

        val originalTodoTask = todoTaskRepository.getById(payload.id!!)
        originalTodoTask?.let {
            val updatedTodoTask = TodoTask(
                id = it.id,
                name = payload.name ?: it.name,
                description = payload.description ?: it.description,
                dueDate = payload.dueDate?.let { dueDate -> Instant.fromEpochMilliseconds(dueDate) } ?: it.dueDate,
                status = payload.status?.let { protoStatus -> Status.fromProto(protoStatus) } ?: it.status,
            )

            val rowsUpdated = todoTaskRepository.update(payload.id, updatedTodoTask)

            if (rowsUpdated == 1) {
                return NetMessage(
                    header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
                    payload = UpdateTodoTaskResponsePayload(ErrorCode.ErrorCode_Success)
                )
            } else {
                return NetMessage(
                    header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
                    payload = UpdateTodoTaskResponsePayload(ErrorCode.ErrorCode_ResourceNotUpdated)
                )
            }
        } ?: return NetMessage(
            header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
            payload = UpdateTodoTaskResponsePayload(ErrorCode.ErrorCode_ResourceDoesNotExist)
        )
    }

    private fun validateRequest(request: NetMessage) {
        require(request.header !== null)
        require(request.header!!.messageType === MessageType.UserInitiatedRequest)
        require(request.header!!.requestType === RequestType.UpdateTodoTask)
        require(request.payload is UpdateTodoTaskRequestPayload)
    }
}