package com.nikitahohulia.listeningplatform.grpc

import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_username.proto.GetUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_username.proto.GetUserByUsernameResponse
import com.nikitahohulia.api.internal.v2.usersvc.user_service.proto.ReactorUserServiceGrpc
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildSuccessResponseGetAll
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildFailureResponseGetAll
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildSuccessResponseGetByUsername
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildFailureResponseGetByUsername
import com.nikitahohulia.listeningplatform.service.UserServiceImpl
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
class GrpcUserService(private val userService: UserServiceImpl) : ReactorUserServiceGrpc.UserServiceImplBase() {

    override fun getAll(request: GetAllUsersRequest): Mono<GetAllUsersResponse> {
        return handleGetAll()
    }

    override fun getByUsername(request: GetUserByUsernameRequest): Mono<GetUserByUsernameResponse> {
        return handleGetByUsername(request.username)
    }

    /*    override fun updateUser(request: UpdateUserRequest): Mono<UpdateUserResponse> {
        return userService.updateUser(request.oldUsername, request.user.toRequest())
            .map { buildSuccessResponseUserUpdated(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.message
                ).toMono()
            }
    }*/

    fun handleGetAll(): Mono<GetAllUsersResponse> {
        return userService.getAllUsers()
            .collectList()
            .map { devices -> buildSuccessResponseGetAll(devices.map { it.toProto() }) }
            .onErrorResume { ex ->
                buildFailureResponseGetAll(
                    ex.javaClass.simpleName,
                    ex.message
                ).toMono()
            }
    }

    fun handleGetByUsername(username: String): Mono<GetUserByUsernameResponse> {
        return userService.getUserByUsername(username)
            .map { buildSuccessResponseGetByUsername(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponseGetByUsername(
                    ex.javaClass.simpleName,
                    ex.message
                ).toMono()
            }
    }
}
