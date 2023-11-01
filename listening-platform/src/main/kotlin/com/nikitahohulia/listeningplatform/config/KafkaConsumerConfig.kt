package com.nikitahohulia.listeningplatform.config

import com.nikitahohulia.api.internal.v2.usersvc.KafkaTopic
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections

@Configuration
class KafkaConsumerConfig (
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String
) {

    @Bean
    fun receiverOptions(): ReceiverOptions<String, ByteArray> {
        return ReceiverOptions.create<String, ByteArray>(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java.name,
                "schema.registry.url" to schemaRegistryUrl,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.GROUP_ID_CONFIG to "ajax"
            )
        ).subscription(Collections.singletonList(KafkaTopic.User.UPDATE))
    }

    @Bean
    fun reactiveKafkaConsumerTemplate(
        receiverOptions: ReceiverOptions<String, ByteArray>
    ): ReactiveKafkaConsumerTemplate<String, ByteArray> {
        return ReactiveKafkaConsumerTemplate(receiverOptions)
    }
}

