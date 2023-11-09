package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.kafka.producer

import com.nikitahohulia.api.internal.v2.usersvc.UserEvent
import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import com.nikitahohulia.listeningplatform.user.application.port.UserEventProducerOutPort
import com.nikitahohulia.listeningplatform.user.domain.User
import com.nikitahohulia.listeningplatform.user.infrastructure.mapper.toProto
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class UserKafkaProducer (
    private val kafkaSenderUpdatedUserEvent: KafkaSender<String, UserUpdatedEvent>
): UserEventProducerOutPort {

    override fun publishEvent(userDomain: User) {
        val userProto = userDomain.toProto()
        val userUpdatedEvent = UserUpdatedEvent.newBuilder().apply {
            user = userProto
        }.build()

        val senderRecord = SenderRecord.create(
            ProducerRecord(
                UserEvent.createUserEventKafkaTopic(UserEvent.UPDATED),
                userProto.id,
                userUpdatedEvent
            ),
            null
        )
        kafkaSenderUpdatedUserEvent.send(senderRecord.toMono()).subscribe()
    }
}
