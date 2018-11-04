package org.boardproject.userservice

import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import org.assertj.core.api.Assertions
import org.boardproject.userservice.UserServiceGRPC
import org.boardproject.userservice.api.FindAllRequest
import org.boardproject.userservice.api.FindOneRequest
import org.boardproject.userservice.api.UserReply
import org.boardproject.userservice.api.UserServiceGrpc
import org.boardproject.userservice.User
import org.boardproject.userservice.UserRepository
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
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class UserServiceGRPCTest {
    private lateinit var server: Server
    private lateinit var channel: ManagedChannel

    @Mock
    private lateinit var userRepositoryMock: UserRepository

    @BeforeEach
    internal fun setUp() {
        val serverName = InProcessServerBuilder.generateName()

        server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(UserServiceGRPC(userRepositoryMock))
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
        `when`(userRepositoryMock.findAll())
                .thenReturn(
                        Arrays.asList(
                                User("1", "eugene.karanda"),
                                User("2", "irina.krivenko")

                        ).toFlux()
                )

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
    }

    @Test
    fun findOne() {
        `when`(userRepositoryMock.findById("1"))
                .thenReturn(
                        User("1", "eugene.karanda")
                                .toMono()
                )

        val blockingStub = UserServiceGrpc.newBlockingStub(channel)
        val reply = blockingStub.findOne(
                FindOneRequest.newBuilder()
                        .setId("1")
                        .build()
        )

        Assertions.assertThat(reply)
                .isEqualTo(
                        UserReply.newBuilder()
                                .setId("1")
                                .setUsername("eugene.karanda")
                                .build()
                )

        verify(userRepositoryMock).findById("1")
    }
}