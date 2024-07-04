package io.sleekflow.infrastructure.database.repositories

import io.sleekflow.domain.User
import io.sleekflow.infrastructure.database.tables.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository(database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(UserTable)
        }
    }

    suspend fun insert(user: User) =
        newSuspendedTransaction(Dispatchers.IO) {
            UserTable.insert {
                it[id] = user.id
                it[name] = user.name
                it[passwordHash] = user.passwordHash
            }[UserTable.id]
        }

    suspend fun update(user: User) =
        newSuspendedTransaction(Dispatchers.IO) {
            UserTable.update({ UserTable.id eq user.id }) {
                it[id] = user.id
                it[name] = user.name
                it[passwordHash] = user.passwordHash
            }
        }

    suspend fun delete(id: Int) =
        newSuspendedTransaction(Dispatchers.IO) {
            UserTable.deleteWhere { UserTable.id eq id }
        }

    suspend fun getById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        UserTable.select { UserTable.id eq id }.map {
            User(
                it[UserTable.id],
                it[UserTable.name],
                it[UserTable.passwordHash],
            )
        }.singleOrNull()
    }

    suspend fun getByName(name: String) = newSuspendedTransaction(Dispatchers.IO) {
        UserTable.select { UserTable.name eq name }.map {
            User(
                it[UserTable.id],
                it[UserTable.name],
                it[UserTable.passwordHash],
            )
        }.singleOrNull()
    }
}