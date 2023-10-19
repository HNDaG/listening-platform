package com.nikitahohulia.listeningplatform.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Message
import reactor.core.publisher.Mono

interface NatsController<ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> {

    val subject: String

    val connection: Connection

    val parser: Parser<ReqT>

    fun handleHelper(request: ReqT): Mono<RespT>

    fun handle(message: Message): Mono<RespT> = handleHelper(parser.parseFrom(message.data))
}
