package com.nikitahohulia.listeningplatform.config

import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

    @Bean
    fun natsConnection(@Value("\${nats.url}") natsUrl: String): Connection = Nats.connect(natsUrl)
}
