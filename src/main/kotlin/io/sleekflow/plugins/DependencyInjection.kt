package io.sleekflow.plugins

import io.ktor.server.application.*
import io.sleekflow.application.CreateTodoTaskRequestHandler
import io.sleekflow.application.DeleteTodoTaskRequestHandler
import io.sleekflow.application.GetTodoTaskRequestHandler
import io.sleekflow.application.UpdateTodoTaskRequestHandler
import io.sleekflow.infrastructure.database.repositories.TodoTaskRepository
import io.sleekflow.infrastructure.database.repositories.UserRepository
import io.sleekflow.infrastructure.network.ConnectionManager
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

private val appModule = module {
    single<CreateTodoTaskRequestHandler> { CreateTodoTaskRequestHandler(get()) }
    single<DeleteTodoTaskRequestHandler> { DeleteTodoTaskRequestHandler(get()) }
    single<UpdateTodoTaskRequestHandler> { UpdateTodoTaskRequestHandler(get()) }
    single<GetTodoTaskRequestHandler> { GetTodoTaskRequestHandler(get()) }

    single<TodoTaskRepository> { TodoTaskRepository(get()) }
    single<UserRepository> { UserRepository(get()) }

    single<ConnectionManager> { ConnectionManager() }

    single<Database> { h2Database }
//    single<Database> { postgresDatabase }
}

// an embedded in-memory db for quick testing and demo
private val h2Database by lazy {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
}

// use this to connect to a postgres database.
// This requires running a postgres database separately.
// For ease of set-up, h2 is used instead.
private val postgresDatabase by lazy {
    Database.connect(
        "jdbc:postgresql://localhost:12346/test",
        driver = "org.postgresql.Driver",
        user = "root",
        password = "your_pwd"
    )
}

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}