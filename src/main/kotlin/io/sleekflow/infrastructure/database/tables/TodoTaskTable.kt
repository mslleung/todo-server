package io.sleekflow.infrastructure.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object TodoTaskTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50).index()
    val description: Column<String> = text("description")
    val dueDate: Column<Long> = long("due_date").index()
    val status: Column<Int> = integer("status").index()
    override val primaryKey = PrimaryKey(id)
}
