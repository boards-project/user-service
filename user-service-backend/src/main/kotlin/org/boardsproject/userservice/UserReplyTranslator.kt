package org.boardsproject.userservice

import org.boardproject.userservice.api.UserReply
import org.springframework.stereotype.Service

interface UserReplyTranslator {
    fun translate(user: User): UserReply
}

@Service
class UserReplyTranslatorDefault : UserReplyTranslator {
    override fun translate(user: User): UserReply = with(user) {
        return UserReply.newBuilder()
                .setId(id)
                .setUsername(username)
                .build()
    }

}