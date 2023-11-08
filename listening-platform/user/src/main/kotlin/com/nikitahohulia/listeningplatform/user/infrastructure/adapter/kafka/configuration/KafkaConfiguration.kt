package com.nikitahohulia.listeningplatform.user.infrastructure.adapter.kafka.configuration

import com.nikitahohulia.api.internal.v2.usersvc.UserEvent
import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import com.nikitahohulia.listeningplatform.core.infrastructure.configuration.kafka.CoreKafkaConfiguration
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.sender.KafkaSender

@Configuration
class KafkaConfiguration (
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String
): CoreKafkaConfiguration(bootstrapServers, schemaRegistryUrl) {

    @Bean
    fun kafkaReceiverUserUpdatedEvent(): KafkaReceiver<String, UserUpdatedEvent> {
        val customProperties: MutableMap<String, Any> = mutableMapOf(
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to UserUpdatedEvent::class.java.name
        )
        return createKafkaReceiver(
            consumerProperties(customProperties),
            UserEvent.createUserEventKafkaTopic(UserEvent.UPDATED),
            "ajax"
        )
    }
    @Bean
    fun kafkaSenderUpdatedUserEvent(): KafkaSender<String, UserUpdatedEvent> =
         createKafkaSender(producerProperties())
}
