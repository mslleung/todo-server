package io.sleekflow.infrastructure.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50).index()
    val passwordHash: Column<ByteArray> = binary("passwordHash")
    override val primaryKey = PrimaryKey(id)
}