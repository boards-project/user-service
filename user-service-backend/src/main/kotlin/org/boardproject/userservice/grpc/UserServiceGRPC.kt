package org.boardproject.userservice.grpc

import io.grpc.stub.StreamObserver
import org.boardproject.userservice.api.FindAllRequest
import org.boardproject.userservice.api.FindOneRequest
import org.boardproject.userservice.api.UserReply
import org.boardproject.userservice.api.UserServiceGrpc
import org.boardproject.userservice.repository.UserRepository
import org.springframework.stereotype.Service

@Service
data class UserServiceGRPC(private val userRepository: UserRepository) : UserServiceGrpc.UserServiceImplBase() {

    override fun findAll(request: FindAllRequest, responseObserver: StreamObserver<UserReply>) {
        userRepository.findAll()
                .map {
                    UserReply.newBuilder()
                            .setId(it.id)
                            .setUsername(it.username)
                            .build()
                }
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                )
    }

    override fun findOne(request: FindOneRequest, responseObserver: StreamObserver<UserReply>) {
        userRepository.findById(request.id)
                .map {
                    UserReply.newBuilder()
                            .setId(it.id)
                            .setUsername(it.username)
                            .build()
                }
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                )
    }
}