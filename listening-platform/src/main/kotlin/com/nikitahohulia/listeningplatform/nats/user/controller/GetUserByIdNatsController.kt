package com.nikitahohulia.listeningplatform.nats.user.controller

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByIdRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByIdResponse
import com.nikitahohulia.listeningplatform.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetUserByIdNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<GetUserByIdRequest, GetUserByIdResponse> {

    override val subject = NatsSubject.User.GET_BY_ID
    override val parser: Parser<GetUserByIdRequest> = GetUserByIdRequest.parser()

    override fun handleHelper(request: GetUserByIdRequest): Mono<GetUserByIdResponse> {
        val id = request.userId

        return userService.getUserById(id)
            .map { buildSuccessResponse(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono()
            }
    }

    private fun buildSuccessResponse(user: User): GetUserByIdResponse =
        GetUserByIdResponse.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetUserByIdResponse =
        GetUserByIdResponse.newBuilder().apply {
            failureBuilder
                .setMessage("GetUserById failed by $exception: $message")
        }.build()
}
