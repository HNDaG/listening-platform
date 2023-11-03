package com.nikitahohulia.listeningplatform.service.event

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import reactor.core.publisher.Flux

interface UserUpdatedEventService <EventT : GeneratedMessageV3> {

    val parser: Parser<EventT>

    fun subscribeToEvents(userId: String, eventType: String): Flux<EventT>

    fun publishEvent(updatedUser: User)
}
