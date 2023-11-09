package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.kafka.consumer

import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import com.nikitahohulia.listeningplatform.user.application.port.UserUpdatedEventProducerOutPort
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver

@Component
class UserKafkaReceiver(
    private val kafkaReceiverUserUpdatedEvent: KafkaReceiver<String, UserUpdatedEvent>,
    @Qualifier("userUpdatedNatsEventServiceOutPort") private val userUpdatedEventSvc: UserUpdatedEventProducerOutPort
) {

    @PostConstruct
    fun init() {
        kafkaReceiverUserUpdatedEvent.receiveAutoAck()
            .flatMap { fluxRecord ->
                fluxRecord
                    .map {
                        userUpdatedEventSvc.publishEvent(it.value().user)
                    }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
