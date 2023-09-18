package com.nikitahohulia.listeningplatform.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {

    @Bean
    fun logger(): Logger = LoggerFactory.getLogger(Logger::class.java)
}
