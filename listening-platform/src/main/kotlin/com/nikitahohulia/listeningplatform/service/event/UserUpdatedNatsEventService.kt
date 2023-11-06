package com.nikitahohulia.listeningplatform.service.event

import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.UserEvent.UPDATED
import com.nikitahohulia.api.internal.v2.usersvc.UserEvent.createUserEventNatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import io.nats.client.Connection
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class UserUpdatedNatsEventService(
    private val connection: Connection
) : UserUpdatedEventService<UserUpdatedEvent> {

    override val parser: Parser<UserUpdatedEvent> = UserUpdatedEvent.parser()

    private val dispatcher = connection.createDispatcher()

    override fun subscribeToEvents(userId: String, eventType: String): Flux<UserUpdatedEvent> {
        return Flux.create { sink ->
            dispatcher.apply {
                subscribe(createUserEventNatsSubject(userId, eventType)) { message ->
                    val parsedData = parser.parseFrom(message.data)
                    sink.next(parsedData)
                }
            }
        }
    }

    override fun publishEvent(updatedUser: User) {
        val updateEventSubject = createUserEventNatsSubject(updatedUser.id, UPDATED)
        val eventMessage = UserUpdatedEvent.newBuilder()
            .setUser(updatedUser)
            .build()

        connection.publish(updateEventSubject, eventMessage.toByteArray())
    }
}
