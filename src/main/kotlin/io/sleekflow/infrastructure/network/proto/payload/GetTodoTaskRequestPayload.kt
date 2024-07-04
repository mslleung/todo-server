package io.sleekflow.infrastructure.network.proto.payload

import io.sleekflow.infrastructure.network.proto.payload.messages.Status
import kotlinx.serialization.Serializable

@Serializable
data class GetTodoTaskFilter(
    val dueDateRangeStart: Long?,
    val dueDateRangeEnd: Long?,
    val statusList: List<Status>?
)

@Serializable
data class GetTodoTaskSort(
    val sortBy: GetTodoTaskSortBy?,
    val sortOrder: GetTodoTaskSortOrder?,
)

@Serializable
enum class GetTodoTaskSortBy {
    GetTodoTaskSortBy_Unspecified,
    GetTodoTaskSortBy_ByName,
    GetTodoTaskSortBy_ByDueDate,
    GetTodoTaskSortBy_ByStatus
}

@Serializable
enum class GetTodoTaskSortOrder {
    GetTodoTaskSortOrder_Unspecified,
    GetTodoTaskSortOrder_ASC,
    GetTodoTaskSortOrder_DESC
}

@Serializable
class GetTodoTaskRequestPayload(
    // TODO pagination?
    val filter: GetTodoTaskFilter?,
    val sort: GetTodoTaskSort?
) : Payload()
