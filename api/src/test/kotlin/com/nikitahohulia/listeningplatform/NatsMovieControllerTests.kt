package com.nikitahohulia.listeningplatform

import com.nikitahohulia.listeningplatform.dto.request.toRequest
import com.nikitahohulia.listeningplatform.dto.response.toProto
import com.nikitahohulia.listeningplatform.repository.CustomUserRepository
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import com.nikitahohulia.nats.NatsSubject.User.CREATE
import com.nikitahohulia.nats.NatsSubject.User.DELETE_BY_ID
import com.nikitahohulia.nats.NatsSubject.User.DELETE_BY_USERNAME
import com.nikitahohulia.nats.NatsSubject.User.GET_ALL
import com.nikitahohulia.nats.NatsSubject.User.GET_BY_ID
import com.nikitahohulia.nats.NatsSubject.User.GET_BY_USERNAME
import com.nikitahohulia.nats.NatsSubject.User.UPDATE
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserRequest
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserResponse
import com.nikitahohulia.nats.reqreply.user.get_all.proto.GetAllUsersResponse
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdResponse
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByIdRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByIdResponse
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameResponse
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.nikitahohulia.nats.commonmodels.user.UserList
import com.nikitahohulia.nats.reqreply.user.create.proto.UpdateUserRequest
import com.nikitahohulia.nats.reqreply.user.create.proto.UpdateUserResponse
import com.nikitahohulia.nats.reqreply.user.get_all.proto.GetAllUsersRequest
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameResponse

@SpringBootTest
class NatsMovieControllerTests {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var userRepository: CustomUserRepository

    @AfterEach
    fun clean() {
        userRepository.deleteById(ObjectId(userId))
    }

    private val userId = "651c6a8763d50fb3f7f1ec7b"

    private val userToAdd: com.nikitahohulia.listeningplatform.entity.User =
        com.nikitahohulia.listeningplatform.entity.User(
            id = ObjectId(userId),
            username = "NatsTestUser",
            password = "123password",
            email = "nats@gmail.com"
        )

    @Test
    fun addUserTestOk() {
        val proto = userToAdd.toProto()
        val message = CreateUserRequest.newBuilder().setUser(proto).build()

        val actual = doRequest(CREATE, message, CreateUserResponse.parser())
        val expected = CreateUserResponse.newBuilder().apply {
            successBuilder.setUser(proto)
        }.build()

        assertThat(expected.success.user.toRequest()).isEqualTo(actual.success.user.toRequest())
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
        userRepository.save(userToAdd)

        val message = GetUserByIdRequest.newBuilder().setUserId(userId).build()

        val actual = doRequest(
            GET_BY_ID,
            message,
            GetUserByIdResponse.parser()
        )

        val expected = GetUserByIdResponse.newBuilder().apply {
            successBuilder
                .setUser(userToAdd.toProto())
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun updateUserTestOk() {
        userRepository.save(userToAdd)

        val proto = userToAdd.copy(
            username = "Updated",
            email = "new.update.com",
            publisherId = ObjectId("651c6a8763d50fb3f7f1ec7c")
        ).toProto()

        val message = UpdateUserRequest.newBuilder().setUser(proto).build()

        val actual = doRequest(UPDATE, message, UpdateUserResponse.parser())
        val expected = UpdateUserResponse.newBuilder().apply {
            successBuilder.setUser(proto)
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun getUserByUsernameTestOk() {
        userRepository.save(userToAdd)

        val message = GetUserByUsernameRequest.newBuilder().setUsername(userToAdd.username).build()

        val actual = doRequest(
            GET_BY_USERNAME,
            message,
            GetUserByUsernameResponse.parser()
        )

        val expected = GetUserByUsernameResponse.newBuilder().apply {
            successBuilder
                .setUser(userToAdd.toProto())
        }.build()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @Order(6)
    fun deleteUserByIdTestOk() {
        userRepository.save(userToAdd)

        val message = DeleteUserByIdRequest.newBuilder().setUserId(userId).build()

        val actual = doRequest(
            DELETE_BY_ID,
            message,
            DeleteUserByIdResponse.parser()
        )

        assertThat(actual.success).isNotNull()
    }

    @Test
    fun deleteUserByUsernameTestFail() {
        userRepository.save(userToAdd)

        val message = DeleteUserByUsernameRequest.newBuilder().setUsername(userToAdd.username).build()

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
            Duration.ofSeconds(5L)
        )
        return parser.parseFrom(response.get().data)
    }
}

