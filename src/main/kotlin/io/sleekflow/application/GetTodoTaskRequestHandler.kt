package io.sleekflow.application

import io.sleekflow.domain.Status
import io.sleekflow.infrastructure.database.repositories.TodoTaskRepository
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import io.sleekflow.infrastructure.network.proto.payload.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.SortOrder

class GetTodoTaskRequestHandler(private val todoTaskRepository: TodoTaskRepository) : IRequestHandler {

    override suspend fun process(request: NetMessage): NetMessage {
        validateRequest(request)

        val payload = request.payload as GetTodoTaskRequestPayload

        val filterSpec = payload.filter?.let {
            TodoTaskRepository.FilterSpec(
                dueDateRangeStart = Instant.fromEpochMilliseconds(it.dueDateRangeStart ?: 0),
                dueDateRangeEnd = it.dueDateRangeEnd?.let { milliSeconds -> Instant.fromEpochMilliseconds(milliSeconds) }
                    ?: Clock.System.now(),
                statusList = it.statusList?.map { protoStatus -> Status.fromProto(protoStatus) }
            )
        }
        val sortSpec = payload.sort?.let {
            TodoTaskRepository.SortSpec(
                sortBy = when (it.sortBy!!) {
                    GetTodoTaskSortBy.GetTodoTaskSortBy_ByName -> TodoTaskRepository.SortBy.Name
                    GetTodoTaskSortBy.GetTodoTaskSortBy_ByDueDate -> TodoTaskRepository.SortBy.DueDate
                    GetTodoTaskSortBy.GetTodoTaskSortBy_ByStatus -> TodoTaskRepository.SortBy.Status
                    GetTodoTaskSortBy.GetTodoTaskSortBy_Unspecified -> throw IllegalArgumentException("Unspecified sort by.")
                },
                sortOrder = when (it.sortOrder!!) {
                    GetTodoTaskSortOrder.GetTodoTaskSortOrder_ASC -> SortOrder.ASC
                    GetTodoTaskSortOrder.GetTodoTaskSortOrder_DESC -> SortOrder.DESC
                    GetTodoTaskSortOrder.GetTodoTaskSortOrder_Unspecified -> throw IllegalArgumentException("Unspecified sort order.")
                }
            )
        }
        val todos = todoTaskRepository.getAll(
            filterSpec,
            sortSpec
        )

        return NetMessage(
            header = request.header!!.copy(messageType = MessageType.UserInitiatedRequestResponse),
            payload = GetTodoTaskResponsePayload(todos.map {
                TodoProtoMessage(
                    it.id!!,
                    it.name,
                    it.description,
                    it.dueDate.toEpochMilliseconds(),
                    it.status.toProto()
                )
            })
        )
    }

    private fun validateRequest(request: NetMessage) {
        require(request.header !== null)
        require(request.header!!.messageType === MessageType.UserInitiatedRequest)
        require(request.header!!.requestType === RequestType.GetTodoTask)
        require(request.payload is GetTodoTaskRequestPayload)
    }
}