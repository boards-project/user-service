package org.boardsproject.userservice

import org.assertj.core.api.Assertions.assertThat
import org.boardsproject.userservice.api.UserReply
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserReplyTranslatorDefaultTest {

    private lateinit var subject: UserReplyTranslatorDefault

    @BeforeEach
    fun setUp() {
        this.subject = UserReplyTranslatorDefault()
    }

    @Test
    fun `translate should convert User to UserReply`() {
        val user = User("12345", "eugene.karanda")
        assertThat(subject.translate(user))
                .isEqualTo(
                        UserReply.newBuilder()
                                .setId("12345")
                                .setUsername("eugene.karanda")
                                .build()
                )
    }
}