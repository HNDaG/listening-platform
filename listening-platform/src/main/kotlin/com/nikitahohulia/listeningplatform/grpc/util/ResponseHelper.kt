package com.nikitahohulia.listeningplatform.grpc.util

import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_username.proto.GetUserByUsernameResponse

object ResponseHelper {

    fun buildSuccessResponseGetByUsername(user: User): GetUserByUsernameResponse {
        return GetUserByUsernameResponse.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()
    }

     fun buildFailureResponseGetByUsername(exception: String, message: String?): GetUserByUsernameResponse {
        return GetUserByUsernameResponse.newBuilder().apply {
            failureBuilder
                .setMessage("GetAllUsers failed by $exception: $message")
        }.build()
    }


     fun buildSuccessResponseGetAll(users: List<User>): GetAllUsersResponse {
        return GetAllUsersResponse.newBuilder().apply {
            successBuilder
                .usersBuilder.addAllUsers(users)
        }.build()
    }

    fun buildFailureResponseGetAll(exception: String, message: String?): GetAllUsersResponse {
        return GetAllUsersResponse.newBuilder().apply {
            failureBuilder
                .setMessage("GetAllUsers failed by $exception: $message")
        }.build()
    }
}
