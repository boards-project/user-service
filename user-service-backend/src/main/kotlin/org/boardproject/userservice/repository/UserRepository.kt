package org.boardproject.userservice.repository

import org.boardproject.userservice.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface UserRepository : ReactiveMongoRepository<User, String>