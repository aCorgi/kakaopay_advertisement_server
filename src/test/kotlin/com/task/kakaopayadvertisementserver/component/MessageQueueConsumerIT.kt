package com.task.kakaopayadvertisementserver.component

import com.task.kakaopayadvertisementserver.config.IntegrationTestBase
import com.task.kakaopayadvertisementserver.dto.message.AdvertisementParticipationCompletedMessageDto
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertNull
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.test.context.TestPropertySource
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Test

@TestPropertySource(properties = ["spring.rabbitmq.listener.simple.auto-startup=true"])
class MessageQueueConsumerIT : IntegrationTestBase() {
    @BeforeEach
    @AfterEach
    fun setUp() {
        amqpAdmin.purgeQueue(rabbitMQProperties.advertisementParticipationMessageQueue.name, false)
    }

    @Nested
    inner class `광고 참여 완료 메시지 consume` {
        @Nested
        inner class `성공` {
            @Test
            fun `광고 참여 완료 메시지를 MQ에 전송하면 Consumer가 받아서 Reward API를 호출한다`() {
                // given
                val messageDto =
                    AdvertisementParticipationCompletedMessageDto(
                        memberId = 1,
                        point = 500,
                    )
                val messageBody = objectMapper.writeValueAsString(messageDto)

                // when
                rewardInternalApiMockWebServer.enqueue(getSuccessMockResponse())

                rabbitTemplate.convertAndSend(
                    rabbitMQProperties.exchange,
                    rabbitMQProperties.advertisementParticipationMessageQueue.routingKey,
                    messageBody,
                )

                // then
                Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofSeconds(1))
                    .untilAsserted {
                        verify(messageQueueConsumer)
                            .receiveAdvertisementParticipationCompletedMessage(
                                eq(messageBody),
                                any(),
                            )

                        assertNull(
                            rewardInternalApiMockWebServer.takeRequest(500, TimeUnit.MILLISECONDS),
                        )
                    }
            }
        }
    }
}
