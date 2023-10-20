package com.nikitahohulia.listeningplatform.user

import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.CREATE
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.GET_BY_ID
import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.DELETE_BY_ID
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.DELETE_BY_USERNAME
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.GET_ALL
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.GET_BY_USERNAME
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.UPDATE
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.CreateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.CreateUserResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.UpdateUserRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.create.proto.UpdateUserResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_all.proto.GetAllUsersResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.DeleteUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.DeleteUserByUsernameResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByIdRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByIdResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByUsernameRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.get_by_id.proto.GetUserByUsernameResponse
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.user.get_by_id.proto.DeleteUserByIdRequest
import com.nikitahohulia.api.internal.v2.usersvc.input.reqreply.user.get_by_id.proto.DeleteUserByIdResponse
import net.datafaker.Faker
import org.bson.types.ObjectId

@E2ETest
class NatsUserControllerE2E {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var userRepository: CustomUserRepository

    private val faker = Faker()

    private fun getUserToAdd(username: String) = User(
        id = ObjectId(),
        username = username,
        password = faker.internet().password(),
        email = faker.internet().emailAddress()
    )

    @Test
    fun `should create user`() {
        // GIVEN
        val expectedUser = getUserToAdd("create-user-name-${System.nanoTime()}").toProto()
        val expected = CreateUserResponse.newBuilder().apply { successBuilder.setUser(expectedUser) }.build()
        val message = CreateUserRequest.newBuilder().setUser(expectedUser).build()

        // WHEN
        val actual = doRequest(CREATE, message, CreateUserResponse.parser())

        // THEN
        assertThat(actual).isEqualTo(expected)
    }


    @Test
    fun `should return list with at least 1 element`() {
        // GIVEN
        userRepository.save(getUserToAdd("get-all-users-${System.nanoTime()}")).block()!!

        // WHEN
        val actual = doRequest(GET_ALL, GetAllUsersRequest.newBuilder().build(), GetAllUsersResponse.parser())

        // THEN
        assertThat(actual.success.users.usersCount).isGreaterThan(0)
    }

    @Test
    fun `should find user by id when user exist with provided id`() {
        // GIVEN
        val user = userRepository.save(getUserToAdd("find-user-by-id-${System.nanoTime()}")).block()!!
        val expected = GetUserByIdResponse.newBuilder().apply { successBuilder.setUser(user.toProto()) }.build()
        val message = GetUserByIdRequest.newBuilder().setUserId(user.id?.toHexString()).build()

        // WHEN
        val actual = doRequest(GET_BY_ID, message, GetUserByIdResponse.parser())

        // THEN
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should update user`() {
        // GIVEN
        val user = userRepository.save(getUserToAdd("not-updated-user-${System.nanoTime()}")).block()!!
        val updatedProto = getUserToAdd("updated-user-${System.nanoTime()}")
            .copy(id = user.id, publisherId = user.publisherId).toProto()
        val expected = UpdateUserResponse.newBuilder().apply { successBuilder.setUser(updatedProto) }.build()
        val message = UpdateUserRequest.newBuilder().setUser(updatedProto).build()

        // WHEN
        val actual = doRequest(UPDATE, message, UpdateUserResponse.parser())

        // THEN
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should find user by username when user exist with provided username`() {
        // GIVEN
        val user = userRepository.save(getUserToAdd("find-user-by-username-${System.nanoTime()}")).block()!!
        val expected = GetUserByUsernameResponse.newBuilder().apply {
            successBuilder.setUser(user.toProto())
        }.build()
        val message = GetUserByUsernameRequest.newBuilder().setUsername(user.username).build()

        // WHEN
        val actual = doRequest(GET_BY_USERNAME, message, GetUserByUsernameResponse.parser())


        // THEN
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should delete user by id when user exists with provided id`() {
        // GIVEN
        val user = userRepository.save(getUserToAdd("delete-user-by-id-${System.nanoTime()}")).block()!!
        val message = DeleteUserByIdRequest.newBuilder().setUserId(user.id?.toHexString()).build()

        //  WHEN
        val actual = doRequest(DELETE_BY_ID, message, DeleteUserByIdResponse.parser())

        // THEN
        assertThat(actual.hasSuccess()).isTrue()
    }

    @Test
    fun `should delete user by username when user exists with provided username`() {
        // GIVEN
        val user = userRepository.save(getUserToAdd("delete-user-by-id-${System.nanoTime()}")).block()!!
        val message = DeleteUserByUsernameRequest.newBuilder().setUsername(user.username).build()

        //  WHEN
        val actual = doRequest(DELETE_BY_USERNAME, message, DeleteUserByUsernameResponse.parser())

        // THEN
        assertThat(actual.hasSuccess()).isTrue()
    }

    private fun <ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: ReqT,
        parser: Parser<RespT>,
    ): RespT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(2L)
        )
        return parser.parseFrom(response.get().data)
    }
}

