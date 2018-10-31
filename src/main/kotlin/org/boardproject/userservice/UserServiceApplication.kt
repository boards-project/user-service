package org.boardproject.userservice

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.stub.StreamObserver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@SpringBootApplication
class UserServiceApplication {

    @Bean
    fun server(services: List<BindableService>): Server {
        return ServerBuilder.forPort(50051)
                .apply {
                    services.forEach {
                        addService(it)
                    }
                }
                .build()
                .start()
    }
}

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}

@Service
class GreeterImpl : GreeterGrpc.GreeterImplBase() {

    override fun sayHello(req: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
        val reply = HelloReply.newBuilder().setMessage("Hello ${req.name}").build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}