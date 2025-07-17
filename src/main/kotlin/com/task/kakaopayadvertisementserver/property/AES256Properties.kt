package com.task.kakaopayadvertisementserver.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("aes")
data class AES256Properties(
    val algorithm: String,
    val ivSize: Int,
    val secretKey: String,
)
