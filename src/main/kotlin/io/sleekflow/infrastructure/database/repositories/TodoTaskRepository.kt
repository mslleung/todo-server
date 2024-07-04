package io.sleekflow.infrastructure.database.repositories

import io.sleekflow.domain.Status
import io.sleekflow.domain.TodoTask
import io.sleekflow.infrastructure.database.tables.TodoTaskTable
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class TodoTaskRepository(database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(TodoTaskTable)
        }
    }

    suspend fun insert(todoTask: TodoTask) =
        newSuspendedTransaction(Dispatchers.IO) {
            TodoTaskTable.insert {
                it[name] = todoTask.name
                it[description] = todoTask.description
                it[dueDate] = todoTask.dueDate.toEpochMilliseconds()
                it[status] = todoTask.status.ordinal
            }[TodoTaskTable.id]
        }

    suspend fun update(id: Int, todoTask: TodoTask) =
        newSuspendedTransaction(Dispatchers.IO) {
            TodoTaskTable.update({ TodoTaskTable.id eq id }) {
                it[name] = todoTask.name
                it[description] = todoTask.description
                it[dueDate] = todoTask.dueDate.toEpochMilliseconds()
                it[status] = todoTask.status.ordinal
            }
        }

    suspend fun delete(id: Int) =
        newSuspendedTransaction(Dispatchers.IO) {
            TodoTaskTable.deleteWhere { TodoTaskTable.id eq id }
        }

    suspend fun getById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        TodoTaskTable.select { TodoTaskTable.id eq id }.map {
            TodoTask(
                it[TodoTaskTable.id],
                it[TodoTaskTable.name],
                it[TodoTaskTable.description],
                Instant.fromEpochMilliseconds(it[TodoTaskTable.dueDate]),
                Status.entries[it[TodoTaskTable.status]],
            )
        }.singleOrNull()
    }

    data class FilterSpec(
        val dueDateRangeStart: Instant = Instant.fromEpochMilliseconds(0),
        val dueDateRangeEnd: Instant = Clock.System.now(),
        // allow filter by multiple status
        val statusList: List<Status>? = null
    )

    // only allow sort on the following columns (only these columns are indexed)
    enum class SortBy { Name, DueDate, Status }
    data class SortSpec(val sortBy: SortBy, val sortOrder: SortOrder)

    suspend fun getAll(
        filterSpec: FilterSpec?,
        sortSpec: SortSpec?
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val query = TodoTaskTable.selectAll()
        filterSpec?.let {
            query.andWhere {
                TodoTaskTable.dueDate.between(
                    it.dueDateRangeStart.toEpochMilliseconds(),
                    it.dueDateRangeEnd.toEpochMilliseconds()
                )
            }
            it.statusList?.let { statusList ->
                query.andWhere { TodoTaskTable.status inList statusList.map { status -> status.ordinal } }
            }
        }
        val sortedQuery = if (sortSpec === null) {
            query
        } else {
            when (sortSpec.sortBy) {
                SortBy.Name -> query.orderBy(TodoTaskTable.name to sortSpec.sortOrder)
                SortBy.DueDate -> query.orderBy(TodoTaskTable.dueDate to sortSpec.sortOrder)
                SortBy.Status -> query.orderBy(TodoTaskTable.status to sortSpec.sortOrder)
            }
        }
        sortedQuery.map {
            TodoTask(
                it[TodoTaskTable.id],
                it[TodoTaskTable.name],
                it[TodoTaskTable.description],
                Instant.fromEpochMilliseconds(it[TodoTaskTable.dueDate]),
                Status.entries[it[TodoTaskTable.status]],
            )
        }
    }
}