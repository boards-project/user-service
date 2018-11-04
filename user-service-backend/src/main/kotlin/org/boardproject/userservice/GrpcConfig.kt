package org.boardproject.userservice

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcConfig {
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