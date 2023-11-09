package com.nikitahohulia.listeningplatform.user.application.port

import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User

interface UserUpdatedEventProducerOutPort {

    fun publishEvent(userProto: User)
}
