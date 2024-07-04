package io.sleekflow.application

import io.sleekflow.infrastructure.database.repositories.TodoTaskRepository
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import io.sleekflow.infrastructure.network.proto.payload.DeleteTodoTaskRequestPayload
import io.sleekflow.infrastructure.network.proto.payload.DeleteTodoTaskResponsePayload
import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode

class DeleteTodoTaskRequestHandler(private val todoTaskRepository: TodoTaskRepository) : IRequestHandler {

    override suspend fun process(request: NetMessage): NetMessage {
        validateRequest(request)

        val payload = request.payload as DeleteTodoTaskRequestPayload

        val rowsDeleted = todoTaskRepository.delete(payload.id!!)

        return if (rowsDeleted == 1) {
            NetMessage(
                header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
                payload = DeleteTodoTaskResponsePayload(ErrorCode.ErrorCode_Success)
            )
        } else {
            NetMessage(
                header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
                payload = DeleteTodoTaskResponsePayload(ErrorCode.ErrorCode_ResourceDoesNotExist)
            )
        }
    }

    private fun validateRequest(request: NetMessage) {
        require(request.header !== null)
        require(request.header!!.messageType === MessageType.UserInitiatedRequest)
        require(request.header!!.requestType === RequestType.DeleteTodoTask)
        require(request.payload is DeleteTodoTaskRequestPayload)
    }
}