package io.sleekflow.domain

class User(
    val id: Int,
    val name: String,
    val passwordHash: ByteArray
) {
}