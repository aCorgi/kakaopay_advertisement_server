package com.task.kakaopayadvertisementserver.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

class PasswordConfigurationIT : IntegrationTestBase() {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `PasswordEncoder는 BCryptPasswordEncoder이어야 한다`() {
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder::class.java)
    }
}
