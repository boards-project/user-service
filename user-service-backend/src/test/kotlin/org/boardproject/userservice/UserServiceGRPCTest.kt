package org.boardproject.userservice

import com.nhaarman.mockitokotlin2.any
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import org.assertj.core.api.Assertions
import org.boardproject.userservice.api.FindAllRequest
import org.boardproject.userservice.api.FindOneRequest
import org.boardproject.userservice.api.UserReply
import org.boardproject.userservice.api.UserServiceGrpc
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class UserServiceGRPCTest {
    private lateinit var server: Server
    private lateinit var channel: ManagedChannel

    @Mock
    private lateinit var userRepositoryMock: UserRepository

    @Mock
    private lateinit var userReplyTranslatorMock: UserReplyTranslator

    @BeforeEach
    internal fun setUp() {
        val serverName = InProcessServerBuilder.generateName()

        val userServiceGRPC = UserServiceGRPC(userRepositoryMock, userReplyTranslatorMock)

        server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(userServiceGRPC)
                .build()
                .start()

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build()
    }

    @AfterEach
    internal fun tearDown() {
        channel.shutdown()
        server.shutdown()
        // fail the test if cannot gracefully shutdown
        try {
            assert(channel.awaitTermination(5, TimeUnit.SECONDS)) {
                "channel cannot be gracefully shutdown"
            }
            assert(server.awaitTermination(5, TimeUnit.SECONDS)) {
                "server cannot be gracefully shutdown"
            }
        } finally {
            channel.shutdownNow()
            server.shutdownNow()
        }
    }

    @Test
    fun findAll() {
        val users = listOf(
                User("1", "eugene.karanda"),
                User("2", "irina.krivenko")

        )

        val userReplies = listOf(
                UserReply.newBuilder()
                        .setId("1")
                        .setUsername("eugene.karanda")
                        .build(),
                UserReply.newBuilder()
                        .setId("2")
                        .setUsername("irina.krivenko")
                        .build()
        )

        `when`(userRepositoryMock.findAll())
                .thenReturn(
                        users.toFlux()
                )
        `when`(userReplyTranslatorMock.translate(any()))
                .thenReturn(userReplies[0])
                .thenReturn(userReplies[1])

        val blockingStub = UserServiceGrpc.newBlockingStub(channel)
        val reply = blockingStub.findAll(FindAllRequest.getDefaultInstance())

        Assertions.assertThat(reply)
                .containsExactly(
                        UserReply.newBuilder()
                                .setId("1")
                                .setUsername("eugene.karanda")
                                .build(),
                        UserReply.newBuilder()
                                .setId("2")
                                .setUsername("irina.krivenko")
                                .build()
                )

        verify(userRepositoryMock).findAll()
        verify(userReplyTranslatorMock).translate(users[0])
        verify(userReplyTranslatorMock).translate(users[1])
    }

    @Test
    fun findOne() {
        val user = User("1", "eugene.karanda")
        val userReply = UserReply.newBuilder()
                .setId("1")
                .setUsername("eugene.karanda")
                .build()

        `when`(userRepositoryMock.findById("1"))
                .thenReturn(
                        user.toMono()
                )

        `when`(userReplyTranslatorMock.translate(user))
                .thenReturn(userReply)

        val blockingStub = UserServiceGrpc.newBlockingStub(channel)
        val reply = blockingStub.findOne(
                FindOneRequest.newBuilder()
                        .setId("1")
                        .build()
        )

        Assertions.assertThat(reply)
                .isEqualTo(userReply)

        verify(userRepositoryMock).findById("1")
        verify(userReplyTranslatorMock).translate(user)
    }
}