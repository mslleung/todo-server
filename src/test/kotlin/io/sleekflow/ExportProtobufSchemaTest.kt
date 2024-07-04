package io.sleekflow

import io.ktor.server.testing.*
import io.sleekflow.infrastructure.network.proto.NetMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.schema.ProtoBufSchemaGenerator
import kotlin.test.Test

class ExportProtobufSchemaTest {

    // generate .proto for front-end to use
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `export proto2`() {
        val descriptors = listOf(NetMessage.serializer().descriptor)
        val schemas = ProtoBufSchemaGenerator.generateSchemaText(descriptors)
        println(schemas)
    }
}