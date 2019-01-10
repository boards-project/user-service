package org.boardsproject.userservice

import org.boardproject.userservice.api.FindAllRequest
import org.boardproject.userservice.api.FindOneRequest
import org.boardproject.userservice.api.ReactorUserServiceGrpc
import org.boardproject.userservice.api.UserReply
import org.lognet.springboot.grpc.GRpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@GRpcService
data class UserServiceGRPC(
        private val userRepository: UserRepository,
        private val userReplyTranslator: UserReplyTranslator
) : ReactorUserServiceGrpc.UserServiceImplBase() {

    override fun findAll(request: Mono<FindAllRequest>): Flux<UserReply> {
        return userRepository.findAll()
                .map {
                    userReplyTranslator.translate(it)
                }
    }

    override fun findOne(request: Mono<FindOneRequest>): Mono<UserReply> {
        return request.map { it.id }
                .flatMap { userRepository.findById(it) }
                .map { userReplyTranslator.translate(it) }
    }
}