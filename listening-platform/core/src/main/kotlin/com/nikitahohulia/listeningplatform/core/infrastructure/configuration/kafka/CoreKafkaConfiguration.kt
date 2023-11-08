package com.nikitahohulia.listeningplatform.core.infrastructure.configuration.kafka

import com.google.protobuf.GeneratedMessageV3
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

open class CoreKafkaConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String
) {
    protected fun <T : GeneratedMessageV3> createKafkaSender(properties: MutableMap<String, Any>):
            KafkaSender<String, T> =
        KafkaSender.create(SenderOptions.create(properties))

    protected fun <T : GeneratedMessageV3> createKafkaReceiver(
        properties: MutableMap<String, Any>,
        topic: String,
        groupId: String
    ): KafkaReceiver<String, T> {
        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        val options =
            ReceiverOptions.create<String, T>(properties).subscription(setOf(topic))
        return KafkaReceiver.create(options)
    }

    protected fun producerProperties(
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

    protected fun consumerProperties(
        customProperties: MutableMap<String, Any> = mutableMapOf()
    ): MutableMap<String, Any> {
        val baseProperties: MutableMap<String, Any> = mutableMapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )
        baseProperties.putAll(customProperties)
        return baseProperties
    }
}
