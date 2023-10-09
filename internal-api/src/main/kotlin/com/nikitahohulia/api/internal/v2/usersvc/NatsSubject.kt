package com.nikitahohulia.api.internal.v2.usersvc

object NatsSubject {

    private const val VERSION = "v2"

    private const val SERVICE_NAME = "listening_platform"

    object User {
        private const val PREFIX = "$VERSION.$SERVICE_NAME"

        private const val SUBDOMAIN = "user"

        const val CREATE = "$PREFIX.input.reqreply.$SUBDOMAIN.create"
        const val GET_ALL = "$PREFIX.input.reqreply.$SUBDOMAIN.get_all"
        const val GET_BY_ID = "$PREFIX.input.reqreply.$SUBDOMAIN.get_by_id"
        const val GET_BY_USERNAME = "$PREFIX.input.reqreply.$SUBDOMAIN.get_by_username"
        const val UPDATE = "$PREFIX.input.reqreply.$SUBDOMAIN.update"
        const val DELETE_BY_ID = "$PREFIX.input.reqreply.$SUBDOMAIN.delete_by_id"
        const val DELETE_BY_USERNAME = "$PREFIX.input.reqreply.$SUBDOMAIN.delete_by_username"
    }
}
