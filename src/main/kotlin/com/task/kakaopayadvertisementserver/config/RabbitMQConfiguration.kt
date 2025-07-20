package com.task.kakaopayadvertisementserver.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.kakaopayadvertisementserver.property.RabbitMQProperties
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration(
    private val objectMapper: ObjectMapper,
    private val rabbitMQProperties: RabbitMQProperties,
) {
    @Bean
    fun advertisementParticipationMessageQueue(): Queue {
        return Queue(rabbitMQProperties.advertisementParticipationMessageQueue.name, true)
    }

    @Bean
    fun messageExchange(): DirectExchange {
        return DirectExchange(rabbitMQProperties.exchange)
    }

    @Bean
    fun advertisementParticipationMessageBinding(
        advertisementParticipationMessageQueue: Queue,
        exchange: DirectExchange,
    ): Binding {
        return BindingBuilder
            .bind(advertisementParticipationMessageQueue)
            .to(exchange)
            .with(rabbitMQProperties.advertisementParticipationMessageQueue.routingKey)
    }

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter(objectMapper)
    }
}
