package com.nikitahohulia.listeningplatform.core.infrastructure.configuration.beanpostprocessor

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

@Configuration
class BeanPostProcessorConfig {

    @Bean
    fun scheduler(): Scheduler = Schedulers.boundedElastic()
}
