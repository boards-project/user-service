package org.boardproject.userservice.grpc

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
import java.util.concurrent.TimeUnit

class UserServiceTest {
    private lateinit var server: Server
    private lateinit var channel: ManagedChannel

    @BeforeEach
    internal fun setUp() {
        val serverName = InProcessServerBuilder.generateName()

        server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(UserService())
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
    }

    @Test
    fun findOne() {
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
    }
}