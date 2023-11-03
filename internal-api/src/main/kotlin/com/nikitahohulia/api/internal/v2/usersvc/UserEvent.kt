package com.nikitahohulia.api.internal.v2.usersvc

object UserEvent {

    private const val VERSION = "v2"

    private const val SERVICE_NAME = "listening_platform"

    private const val SUBDOMAIN = "user"

    private const val PREFIX = "$VERSION.$SERVICE_NAME"

    const val UPDATED = "updated"


    fun createUserEventKafkaTopic(eventType: String): String =
        "$PREFIX.output.pubsub.$SUBDOMAIN.$eventType"


    fun createUserEventNatsSubject(userId: String, eventType: String): String =
        "$PREFIX.output.pubsub.$SUBDOMAIN.$userId.$eventType"

}
