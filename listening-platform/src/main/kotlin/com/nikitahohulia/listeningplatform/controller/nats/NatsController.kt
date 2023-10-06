package com.nikitahohulia.listeningplatform.controller.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection

interface NatsController<ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> {

    val subject: String

    val connection: Connection

    val parser: Parser<ReqT>

    fun handle(request: ReqT): RespT
}
