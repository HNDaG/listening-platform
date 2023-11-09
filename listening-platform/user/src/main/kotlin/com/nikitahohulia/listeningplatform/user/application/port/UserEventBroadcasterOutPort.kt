package com.nikitahohulia.listeningplatform.user.application.port

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.user.domain.User
import reactor.core.publisher.Flux

interface UserEventBroadcasterOutPort <EventT : GeneratedMessageV3> {

    val parser: Parser<EventT>

    fun subscribeToEvents(id: String, eventType: String): Flux<EventT>

    fun publishEvent(userDomain: User)
}
