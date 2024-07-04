package io.sleekflow.domain

import kotlinx.datetime.Instant
import io.sleekflow.infrastructure.network.proto.payload.messages.Status as ProtoStatus

enum class Status {
    NotStarted, InProgress, Completed;

    companion object {
        fun fromProto(protoStatus: ProtoStatus): Status {
            return when (protoStatus) {
                ProtoStatus.Status_Unspecified -> throw IllegalArgumentException("Status cannot be unspecified.")

                ProtoStatus.Status_NotStarted -> NotStarted
                ProtoStatus.Status_InProgress -> InProgress
                ProtoStatus.Status_Completed -> Completed
            }
        }
    }

    fun toProto(): ProtoStatus {
        return when (this) {
            NotStarted -> ProtoStatus.Status_NotStarted
            InProgress -> ProtoStatus.Status_InProgress
            Completed -> ProtoStatus.Status_Completed
        }
    }
}

class TodoTask(
    val id: Int?,
    val name: String,
    val description: String,
    val dueDate: Instant,
    val status: Status,
    // TODO priority, tags...
) {
    init {
        require(name.length < 50)
    }
}