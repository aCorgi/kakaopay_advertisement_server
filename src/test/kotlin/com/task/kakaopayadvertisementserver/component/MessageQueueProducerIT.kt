package com.task.kakaopayadvertisementserver.component

import com.task.kakaopayadvertisementserver.config.IntegrationTestBase
import com.task.kakaopayadvertisementserver.dto.message.AdvertisementParticipationCompletedMessageDto
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.test.context.TestPropertySource
import java.time.Duration
import kotlin.test.Test

@TestPropertySource(properties = ["spring.rabbitmq.listener.simple.auto-startup=true"])
class MessageQueueProducerIT : IntegrationTestBase() {
    @BeforeEach
    @AfterEach
    fun setUp() {
        amqpAdmin.purgeQueue(rabbitMQProperties.advertisementParticipationMessageQueue.name, false)
    }

    @Nested
    inner class `광고 참여 완료 메시지 publish` {
        @Nested
        inner class `성공` {
            @Test
            fun `광고 참여 완료 메시지를 MQ에 publish 한다`() {
                // given
                val messageDto =
                    AdvertisementParticipationCompletedMessageDto(
                        memberId = 12,
                        point = 5004,
                    )
                rewardInternalApiMockWebServer.enqueue(getSuccessMockResponse())

                // when
                messageQueueProducer.sendMessage(messageDto)

                // then
                Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofSeconds(1))
                    .untilAsserted {
                        verify(messageQueueConsumer)
                            .receiveAdvertisementParticipationCompletedMessage(
                                eq(objectMapper.writeValueAsString(messageDto)),
                                any(),
                            )
                    }
            }
        }
    }
}
