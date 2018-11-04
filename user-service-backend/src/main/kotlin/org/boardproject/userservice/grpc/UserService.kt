package org.boardproject.userservice.grpc

import io.grpc.stub.StreamObserver
import org.boardproject.userservice.api.FindAllRequest
import org.boardproject.userservice.api.FindOneRequest
import org.boardproject.userservice.api.UserReply
import org.boardproject.userservice.api.UserServiceGrpc
import org.springframework.stereotype.Service

@Service
class UserService : UserServiceGrpc.UserServiceImplBase() {

    override fun findAll(request: FindAllRequest, responseObserver: StreamObserver<UserReply>) {
        val reply1 = UserReply.newBuilder()
                .setId("1")
                .setUsername("eugene.karanda")
                .build()

        val reply2 = UserReply.newBuilder()
                .setId("2")
                .setUsername("irina.krivenko")
                .build()

        responseObserver.onNext(reply1)
        responseObserver.onNext(reply2)
        responseObserver.onCompleted()
    }

    override fun findOne(request: FindOneRequest, responseObserver: StreamObserver<UserReply>) {
        val reply = UserReply.newBuilder()
                .setId("1")
                .setUsername("eugene.karanda")
                .build()

        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}