package com.task.kakaopayadvertisementserver.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("rabbit-mq")
data class RabbitMQProperties(
    val exchange: String,
    val advertisementParticipationMessageQueue: MessageQueueProperties,
) {
    data class MessageQueueProperties(
        val name: String,
        val routingKey: String,
    )
}
