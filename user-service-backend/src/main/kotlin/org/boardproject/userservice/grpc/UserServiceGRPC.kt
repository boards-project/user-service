package org.boardproject.userservice.grpc

import org.boardproject.userservice.api.FindAllRequest
import org.boardproject.userservice.api.FindOneRequest
import org.boardproject.userservice.api.ReactorUserServiceGrpc
import org.boardproject.userservice.api.UserReply
import org.boardproject.userservice.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
data class UserServiceGRPC(private val userRepository: UserRepository) : ReactorUserServiceGrpc.UserServiceImplBase() {

    override fun findAll(request: Mono<FindAllRequest>): Flux<UserReply> {
        return userRepository.findAll()
                .map {
                    UserReply.newBuilder()
                            .setId(it.id)
                            .setUsername(it.username)
                            .build()
                }
    }

    override fun findOne(request: Mono<FindOneRequest>): Mono<UserReply> {
        return request.map { it.id }
                .flatMap { userRepository.findById(it) }
                .map {
                    UserReply.newBuilder()
                            .setId(it.id)
                            .setUsername(it.username)
                            .build()
                }
    }
}