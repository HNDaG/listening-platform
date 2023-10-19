package com.nikitahohulia.listeningplatform.nats

import io.nats.client.Message
import io.nats.client.MessageHandler
import reactor.core.scheduler.Scheduler

class NatsReactiveHandler (
    private val natsController: NatsController<*, *>,
    private val scheduler: Scheduler
) : MessageHandler {

    override fun onMessage(message: Message) {
        natsController.handle(message)
            .map { it.toByteArray() }
            .doOnNext { natsController.connection.publish(message.replyTo, it) }
            .subscribeOn(scheduler)
            .subscribe()
    }
}
