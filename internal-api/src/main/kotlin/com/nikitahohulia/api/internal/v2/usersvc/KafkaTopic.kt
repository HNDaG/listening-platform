package com.nikitahohulia.api.internal.v2.usersvc

object KafkaTopic {

    private const val VERSION = "v2"

    private const val SERVICE_NAME = "listening_platform"

    object User {
        private const val PREFIX = "$VERSION.$SERVICE_NAME"

        private const val SUBDOMAIN = "user"

        const val UPDATE = "$PREFIX.output.pubsub.$SUBDOMAIN.update"
    }
}
