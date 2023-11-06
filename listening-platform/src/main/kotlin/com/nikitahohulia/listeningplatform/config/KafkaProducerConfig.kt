package com.nikitahohulia.listeningplatform.config

import com.google.protobuf.GeneratedMessageV3
import com.nikitahohulia.api.internal.v2.usersvc.output.pubsub.update.proto.UserUpdatedEvent
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaProducerConfig(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String
) {

    @Bean
    fun kafkaSenderUpdatedUserEvent(): KafkaSender<String, UserUpdatedEvent> =
        createKafkaSender(producerProperties())

    private fun <T : GeneratedMessageV3> createKafkaSender(properties: MutableMap<String, Any>):
            KafkaSender<String, T> =
        KafkaSender.create(SenderOptions.create(properties))

    private fun producerProperties(
        customProperties: MutableMap<String, Any> = mutableMapOf()
    ): MutableMap<String, Any> {
        val baseProperties: MutableMap<String, Any> = mutableMapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )
        baseProperties.putAll(customProperties)
        return baseProperties
    }
}
