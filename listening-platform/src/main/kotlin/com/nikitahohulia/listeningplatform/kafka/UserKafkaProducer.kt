package com.nikitahohulia.listeningplatform.kafka

import com.nikitahohulia.api.internal.v2.usersvc.UserEvent
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class UserKafkaProducer (
    private val kafkaSenderUpdatedUserEvent: KafkaSender<String, UserUpdatedEvent>
) {

    fun sendUserUpdatedEventToKafka(userProto: User) {
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
