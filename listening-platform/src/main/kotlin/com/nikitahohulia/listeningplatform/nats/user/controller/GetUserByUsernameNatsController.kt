package com.nikitahohulia.listeningplatform.nats.user.controller

import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.service.UserService
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByUsernameResponse
import com.nikitahohulia.listeningplatform.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetUserByUsernameNatsController(
    override val connection: Connection,
    private val userService: UserService
) : NatsController<GetUserByUsernameRequest, GetUserByUsernameResponse> {

    override val subject = NatsSubject.User.GET_BY_USERNAME
    override val parser: Parser<GetUserByUsernameRequest> = GetUserByUsernameRequest.parser()

    override fun handleHelper(request: GetUserByUsernameRequest): Mono<GetUserByUsernameResponse> {
        val username = request.username

        return userService.getUserByUsername(username)
            .map { buildSuccessResponse(it.toProto()) }
            .onErrorResume { ex ->
                buildFailureResponse(
                    ex.javaClass.simpleName,
                    ex.toString()
                ).toMono()
            }
    }

    private fun buildSuccessResponse(user: User): GetUserByUsernameResponse =
        GetUserByUsernameResponse.newBuilder().apply {
            successBuilder
                .setUser(user)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetUserByUsernameResponse =
        GetUserByUsernameResponse.newBuilder().apply {
            failureBuilder
                .setMessage("GetUserByUsername failed by $exception: $message")
        }.build()
}
