package org.boardproject.userservice

import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class HelloWorldServerTest {
    private lateinit var server: Server
    private lateinit var channel: ManagedChannel

    @BeforeEach
    internal fun setUp() {
        val serverName = InProcessServerBuilder.generateName()

        server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(GreeterImpl())
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
    fun greeterImpl_replyMessage() {
        val blockingStub = GreeterGrpc.newBlockingStub(channel)
        val reply = blockingStub.sayHello(
                HelloRequest.newBuilder()
                        .setName("test name")
                        .build()
        )

        assertEquals("Hello test name", reply.message)
    }
}