package com.nikitahohulia.listeningplatform.grpc

import com.nikitahohulia.api.internal.v2.usersvc.UserEvent
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_username.proto.GetUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_username.proto.GetUserByUsernameResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.update.proto.UpdateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.update.proto.UpdateUserResponse
import com.nikitahohulia.api.internal.v2.usersvc.user_service.proto.ReactorUserServiceGrpc
import com.nikitahohulia.listeningplatform.dto.request.toEntity
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildSuccessResponseGetAll
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildFailureResponseGetAll
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildSuccessResponseGetByUsername
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildFailureResponseGetByUsername
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildFailureResponseUserUpdated
import com.nikitahohulia.listeningplatform.grpc.util.ResponseHelper.buildSuccessResponseUserUpdated
import com.nikitahohulia.listeningplatform.service.UserServiceImpl
import com.nikitahohulia.listeningplatform.service.event.UserUpdatedNatsEventService
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
class GrpcUserService(
    private val userService: UserServiceImpl,
    private val userUpdatedEventService: UserUpdatedNatsEventService
) : ReactorUserServiceGrpc.UserServiceImplBase() {

    override fun getAll(request: GetAllUsersRequest): Mono<GetAllUsersResponse> {
        return handleGetAll()
    }

    override fun getByUsername(request: GetUserByUsernameRequest): Flux<GetUserByUsernameResponse> {
        return handleGetByUsername(request.username)
    }

    override fun updateUser(request: UpdateUserRequest): Mono<UpdateUserResponse> {
        return handleUpdateUser(request.oldUsername, request.user.toEntity())
    }

    fun handleUpdateUser(oldUsername: String, user: User): Mono<UpdateUserResponse> {
        return userService.updateUser(oldUsername, user)
            .map { buildSuccessResponseUserUpdated(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponseUserUpdated(
                    ex.javaClass.simpleName,
                    ex.message
                ).toMono()
            }
    }

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

    fun handleGetByUsername(username: String): Flux<GetUserByUsernameResponse> {
        return userService.getUserByUsername(username)
            .flatMapMany { user ->
                userUpdatedEventService.subscribeToEvents(user.id!!.toHexString(), UserEvent.UPDATED)
                    .map { buildSuccessResponseGetByUsername(it.user) }
                    .startWith(buildSuccessResponseGetByUsername(user.toProto()))
            }
            .onErrorResume { ex ->
                buildFailureResponseGetByUsername(
                    ex.javaClass.simpleName,
                    ex.message
                ).toMono()
            }
    }
}
