package com.nikitahohulia.listeningplatform.bpp

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import com.google.protobuf.GeneratedMessageV3
import com.nikitahohulia.listeningplatform.controller.nats.NatsController
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Message

@Component
class NatsControllerBeanPostProcessor(private val connection: Connection) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            bean.initializeNatsController(connection)
        }
        return bean
    }
}

private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3>
        NatsController<RequestT, ResponseT>.initializeNatsController(
    connection: Connection
) {
    createDispatcher(connection).apply {
        subscribe(subject)
    }
}

private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3>
        NatsController<RequestT, ResponseT>.createDispatcher(
    connection: Connection
): Dispatcher = connection.createDispatcher { message: Message ->
    val parsedData = parser.parseFrom(message.data)
    val response = handle(parsedData)
    connection.publish(message.replyTo, response.toByteArray())
}
