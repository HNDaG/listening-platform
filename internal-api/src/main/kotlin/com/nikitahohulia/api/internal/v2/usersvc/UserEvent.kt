package com.nikitahohulia.api.internal.v2.usersvc

object UserEvent {

    private const val SUBDOMAIN = "user"

    private const val PREFIX = "v2.listening_platform.output.pubsub"

    const val UPDATED = "updated"


    fun createUserEventKafkaTopic(eventType: String): String =
        "$PREFIX.$SUBDOMAIN.$eventType"

    fun createUserEventNatsSubject(userId: String, eventType: String): String =
        "$PREFIX.$SUBDOMAIN.$userId.$eventType"
}
