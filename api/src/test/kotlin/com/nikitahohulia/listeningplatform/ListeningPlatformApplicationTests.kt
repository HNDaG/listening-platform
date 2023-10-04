package com.nikitahohulia.listeningplatform

import com.nikitahohulia.listeningplatform.dto.request.UserDtoRequest
import com.nikitahohulia.listeningplatform.dto.request.toProto
import com.nikitahohulia.listeningplatform.dto.request.toRequest
import io.nats.client.Connection
import org.junit.jupiter.api.Test
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
import com.nikitahohulia.nats.commonmodels.user.User
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserRequestCommon
import com.nikitahohulia.nats.reqreply.user.create.proto.CreateUserResponseCommon
import com.nikitahohulia.nats.reqreply.user.get_all.proto.GetAllUsersResponseCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByIdResponseCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.DeleteUserByUsernameRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByIdRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByIdResponseCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameRequestCommon
import com.nikitahohulia.nats.reqreply.user.get_by_id.proto.GetUserByUsernameResponseCommon
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
class NatsMovieControllerTests {

    @Autowired
    lateinit var natsConnection: Connection

    private lateinit var user: User

    @Test
    @Order(1)
    fun addUserTestOk() {
        val userDtoRequest = UserDtoRequest(
            id = null,
            username = "NatsTestUser",
            password = "123password",
            email = "nats@gmail.com"
        )
        val proto = userDtoRequest.toProto()
        val message = CreateUserRequestCommon.newBuilder().setUser(proto).build()

        val future = natsConnection.requestWithTimeout(CREATE, message.toByteArray(), Duration.ofMillis(5000))

        val reply = CreateUserResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasSuccess())
        user = (reply.success.user?:null)!!
    }

    @Test
    @Order(2)
    fun getAllUsersTestOk() {
        val future = natsConnection.requestWithTimeout(GET_ALL, "".toByteArray(), Duration.ofMillis(3000))
        val reply = GetAllUsersResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasSuccess())
    }

    @Test
    @Order(3)
    fun getUserByIdTestOk() {
        val message = GetUserByIdRequestCommon.newBuilder().setUserId(user.id).build()

        val future = natsConnection.requestWithTimeout(GET_BY_ID, message.toByteArray(), Duration.ofMillis(3000))

        val reply = GetUserByIdResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasSuccess())
        Assertions.assertEquals(reply.success.user.toRequest(), user.toRequest())
    }

    @Test
    @Order(4)
    fun updateUserTestOk() {
        val updateUser = UserDtoRequest(
            id = user.id,
            username = "NatsUserUpdated",
            password = "Updated",
            email = "Updated@gmail.com"
        )
        val proto = updateUser.toProto()
        val message = CreateUserRequestCommon.newBuilder().setUser(proto).build()

        val future = natsConnection.requestWithTimeout(UPDATE, message.toByteArray(), Duration.ofMillis(5000))

        val reply = CreateUserResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasSuccess())
        println(proto.toString())
        println(reply.success.user.toString())
        Assertions.assertEquals(proto.toRequest(), reply.success.user.toRequest())
        user = proto
    }

    @Test
    @Order(5)
    fun getUserByUsernameTestOk() {
        val message = GetUserByUsernameRequestCommon.newBuilder().setUsername(user.username).build()

        val future = natsConnection.requestWithTimeout(GET_BY_USERNAME, message.toByteArray(), Duration.ofMillis(3000))

        val reply = GetUserByUsernameResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasSuccess())
        Assertions.assertEquals(reply.success.user.toRequest(), user.toRequest())
    }

    @Test
    @Order(6)
    fun deleteUserByIdTestOk() {
        val message = DeleteUserByIdRequestCommon.newBuilder().setUserId(user.id).build()

        val future = natsConnection.requestWithTimeout(DELETE_BY_ID, message.toByteArray(), Duration.ofMillis(3000000))

        val reply = DeleteUserByIdResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasSuccess())
    }

    @Test
    @Order(7)
    fun deleteUserByUsernameTestFail() {
        val message = DeleteUserByUsernameRequestCommon.newBuilder().setUsername(user.username).build()

        val future = natsConnection.requestWithTimeout(DELETE_BY_USERNAME, message.toByteArray(), Duration.ofMillis(3000000))

        val reply = DeleteUserByIdResponseCommon.parseFrom(future.get().data)

        Assertions.assertTrue(reply.hasFailure())
    }


}
