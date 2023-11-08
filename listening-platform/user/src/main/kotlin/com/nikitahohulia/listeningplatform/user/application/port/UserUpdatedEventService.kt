package com.nikitahohulia.listeningplatform.user.application.port

import com.google.protobuf.GeneratedMessageV3
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.listeningplatform.core.application.port.EventSubscriber

interface UserUpdatedEventService<EventT : GeneratedMessageV3> : EventSubscriber<EventT> {

    fun publishEvent(updatedUser: User)
}
