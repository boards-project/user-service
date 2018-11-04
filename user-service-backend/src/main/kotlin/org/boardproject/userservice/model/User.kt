package org.boardproject.userservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "user")
data class User(
        @Id val id: String = UUID.randomUUID().toString(),
        @Field val username: String
)