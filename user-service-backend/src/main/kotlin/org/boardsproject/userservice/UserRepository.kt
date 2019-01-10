package org.boardsproject.userservice

import org.boardsproject.userservice.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface UserRepository : ReactiveMongoRepository<User, String>