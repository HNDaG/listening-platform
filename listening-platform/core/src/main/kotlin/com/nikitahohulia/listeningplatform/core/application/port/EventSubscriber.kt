package com.nikitahohulia.listeningplatform.core.application.port

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import reactor.core.publisher.Flux

interface EventSubscriber <EventT : GeneratedMessageV3> {

    val parser: Parser<EventT>

    fun subscribeToEvents(id: String, eventType: String): Flux<EventT>
}
