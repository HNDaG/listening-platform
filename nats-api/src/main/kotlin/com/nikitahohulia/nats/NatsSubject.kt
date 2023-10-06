package com.nikitahohulia.nats

object NatsSubject {

    private const val REQUEST_PREFIX = "lp.api"

    object User {
        private const val USER_PREFIX = "$REQUEST_PREFIX.user"

        const val CREATE = "$USER_PREFIX.create"
        const val GET_ALL = "$USER_PREFIX.get_all"
        const val GET_BY_ID = "$USER_PREFIX.get_by_id"
        const val GET_BY_USERNAME = "$USER_PREFIX.get_by_username"
        const val UPDATE = "$USER_PREFIX.update"
        const val DELETE_BY_ID = "$USER_PREFIX.delete_by_id"
        const val DELETE_BY_USERNAME = "$USER_PREFIX.delete_by_username"
    }
}
