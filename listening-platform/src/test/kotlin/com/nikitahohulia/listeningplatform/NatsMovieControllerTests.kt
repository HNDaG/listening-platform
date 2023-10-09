package com.nikitahohulia.listeningplatform

import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.CREATE
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.DELETE_BY_ID
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.DELETE_BY_USERNAME
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.GET_ALL
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.GET_BY_ID
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.GET_BY_USERNAME
import com.nikitahohulia.api.internal.v2.usersvc.NatsSubject.User.UPDATE
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.nikitahohulia.listeningplatform.entity.User
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.UserList
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
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import com.nikitahohulia.api.internal.v2.usersvc.commonmodels.user.User as NatsUser

@SpringBootTest
class NatsMovieControllerTests {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var userRepository: CustomUserRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @AfterEach
    fun clean() {
        mongoTemplate.remove<User>(Query())
    }

    private val faker = Faker()

    private fun getUserToAdd() = User(
            username = faker.name().username(),
            password = faker.internet().password(),
            email = faker.internet().emailAddress()
    )

    @Test
    fun addUserTestOk() {
        val proto = getUserToAdd().toProto()

        val message = CreateUserRequest.newBuilder().setUser(proto).build()

        val actual = doRequest(CREATE, message, CreateUserResponse.parser())
        val expected = CreateUserResponse.newBuilder().apply {
            successBuilder.setUser(proto.setIdFromCreateUserResponse(actual))
        }.build()

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun getAllUsersTestOk() {
        val actual = doRequest(
            GET_ALL,
            GetAllUsersRequest.newBuilder().build(),
            GetAllUsersResponse.parser()
        )
        val expected = GetAllUsersResponse.newBuilder().apply {
            successBuilder
                .setUsers(UserList.newBuilder().addAllUsers(emptyList()).build())
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun getUserByIdTestOk() {
        val user = userRepository.save(getUserToAdd())!!

        val message = GetUserByIdRequest.newBuilder().setUserId(user.id?.toHexString()).build()

        val actual = doRequest(
            GET_BY_ID,
            message,
            GetUserByIdResponse.parser()
        )

        val expected = GetUserByIdResponse.newBuilder().apply {
            successBuilder
                .setUser(user.toProto())
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun updateUserTestOk() {
        val user = userRepository.save(getUserToAdd())!!
        val updatedProto = getUserToAdd().copy(id = user.id, publisherId = user.publisherId).toProto()

        val message = UpdateUserRequest.newBuilder().setUser(updatedProto).build()

        val actual = doRequest(UPDATE, message, UpdateUserResponse.parser())
        val expected = UpdateUserResponse.newBuilder().apply {
            successBuilder.setUser(updatedProto)
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun getUserByUsernameTestOk() {
        val user = userRepository.save(getUserToAdd())!!

        val message = GetUserByUsernameRequest.newBuilder().setUsername(user.username).build()

        val actual = doRequest(
            GET_BY_USERNAME,
            message,
            GetUserByUsernameResponse.parser()
        )

        val expected = GetUserByUsernameResponse.newBuilder().apply {
            successBuilder
                .setUser(user.toProto())
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun deleteUserByIdTestOk() {
        val user = userRepository.save(getUserToAdd())!!

        val message = DeleteUserByIdRequest.newBuilder().setUserId(user.id?.toHexString()).build()

        val actual = doRequest(
            DELETE_BY_ID,
            message,
            DeleteUserByIdResponse.parser()
        )

        assertThat(actual.success).isNotNull()
    }

    @Test
    fun deleteUserByUsernameTestOk() {
        val user = userRepository.save(getUserToAdd())!!

        val message = DeleteUserByUsernameRequest.newBuilder().setUsername(user.username).build()

        val actual = doRequest(
            DELETE_BY_USERNAME,
            message,
            DeleteUserByUsernameResponse.parser()
        )

        assertThat(actual.success).isNotNull()
    }

    private fun <ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: ReqT,
        parser: Parser<RespT>,
    ): RespT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }

    private fun NatsUser.setIdFromCreateUserResponse(response: CreateUserResponse): NatsUser {
        val builder = NatsUser.newBuilder()
            .setId(response.success.user.id)
            .setEmail(email)
            .setUsername(username)
            .setPassword(password)

        if (!publisherId.isNullOrEmpty()) builder.setPublisherId(publisherId)
        return builder.build()
    }
}

