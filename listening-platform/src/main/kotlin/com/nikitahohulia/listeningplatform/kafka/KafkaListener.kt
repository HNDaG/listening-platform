package com.nikitahohulia.listeningplatform.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class KafkaListener {

    @KafkaListener(topics = ["v2.listening_platform.output.pubsub.user.update"], groupId = "ajax")
    fun listen(message: String) {
        println("Received message from kafka: $message")
    }
}
