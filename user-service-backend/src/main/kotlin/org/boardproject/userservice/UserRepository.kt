package org.boardproject.userservice

import org.boardproject.userservice.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface UserRepository : ReactiveMongoRepository<User, String>