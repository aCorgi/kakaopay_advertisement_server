package com.task.kakaopayadvertisementserver.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.kakaopayadvertisementserver.dto.message.RabbitMqMessageDto
import com.task.kakaopayadvertisementserver.property.RabbitMQProperties
import com.task.kakaopayadvertisementserver.util.logger
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class MessageQueueProducer(
    private val objectMapper: ObjectMapper,
    private val rabbitMQProperties: RabbitMQProperties,
    private val rabbitTemplate: RabbitTemplate,
) {
    val log = logger<MessageQueueProducer>()

    fun sendMessage(messageDto: RabbitMqMessageDto) {
        convertAndSend(
            message = messageDto,
            routingKey = rabbitMQProperties.advertisementParticipationMessageQueue.routingKey,
        )
    }

    private fun convertAndSend(
        message: RabbitMqMessageDto,
        routingKey: String,
    ) {
        val jsonMessage = objectMapper.writeValueAsString(message)

        log.info("메세지 Send: $jsonMessage with routing key: $routingKey")
        rabbitTemplate.convertAndSend(
            rabbitMQProperties.exchange,
            routingKey,
            jsonMessage,
        )
    }
}
