package com.task.kakaopayadvertisementserver.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("api.reward-internal")
data class RewardInternalApiProperties(
    val baseUrl: String,
    val username: String,
    val password: String,
)
