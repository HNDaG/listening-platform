package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.nats.subscriber

import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.UserEvent.UPDATED
import com.nikitahohulia.api.internal.v2.usersvc.UserEvent.createUserEventNatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import com.nikitahohulia.listeningplatform.user.application.port.UserEventBroadcasterOutPort
import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toProto
import io.nats.client.Connection
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class UserUpdatedNatsEventServiceOutPort(
    private val connection: Connection
) : UserEventBroadcasterOutPort<UserUpdatedEvent> {

    override val parser: Parser<UserUpdatedEvent> = UserUpdatedEvent.parser()

    private val dispatcher = connection.createDispatcher()

    override fun subscribeToEvents(id: String, eventType: String): Flux<UserUpdatedEvent> {
        return Flux.create { sink ->
            dispatcher.apply {
                subscribe(createUserEventNatsSubject(id, eventType)) { message ->
                    val parsedData = parser.parseFrom(message.data)
                    sink.next(parsedData)
                }
            }
        }
    }

    override fun publishEvent(userDomain: User) {
        val userProto = userDomain.toProto()
        val updateEventSubject = createUserEventNatsSubject(userProto.id, UPDATED)
        val eventMessage = UserUpdatedEvent.newBuilder()
            .setUser(userProto)
            .build()

        connection.publish(updateEventSubject, eventMessage.toByteArray())
    }
}
