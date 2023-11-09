package com.nikitahohulia.listeningplatform.user.application.port

import com.nikitahohulia.listeningplatform.user.domain.User

interface UserEventProducerOutPort {

    fun publishEvent(userDomain: User)
}
