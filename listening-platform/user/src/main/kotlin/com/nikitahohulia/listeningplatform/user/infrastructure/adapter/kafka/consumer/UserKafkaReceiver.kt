package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.kafka.consumer

import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import com.nikitahohulia.listeningplatform.user.application.port.UserUpdatedEventService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver

@Component
class UserKafkaReceiver(
    private val kafkaReceiverUserUpdatedEvent: KafkaReceiver<String, UserUpdatedEvent>,
    private val userUpdatedEventService: UserUpdatedEventService<UserUpdatedEvent>
) {

    @PostConstruct
    fun init() {
        kafkaReceiverUserUpdatedEvent.receiveAutoAck()
            .flatMap { fluxRecord ->
                fluxRecord
                    .map {
                        userUpdatedEventService.publishEvent(it.value().user)
                    }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}