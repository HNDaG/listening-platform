package com.nikitahohulia.listeningplatform.core.infrastructure.configuration.beanpostprocessor

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import com.nikitahohulia.listeningplatform.core.infrastructure.adapter.nats.NatsController
import com.nikitahohulia.listeningplatform.core.infrastructure.configuration.beanpostprocessor.util.NatsReactiveHandler
import reactor.core.scheduler.Scheduler

@Component
class NatsControllerBeanPostProcessor(private val scheduler: Scheduler) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            val reactiveHandler = NatsReactiveHandler(bean, scheduler)
            bean.connection
                .createDispatcher(reactiveHandler)
                .subscribe(bean.subject)
        }
        return bean
    }
}
