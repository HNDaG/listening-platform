package com.nikitahohulia.listeningplatform.bpp

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import com.google.protobuf.GeneratedMessageV3
import com.nikitahohulia.listeningplatform.nats.NatsController
import com.nikitahohulia.listeningplatform.nats.NatsReactiveHandler
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Message
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
