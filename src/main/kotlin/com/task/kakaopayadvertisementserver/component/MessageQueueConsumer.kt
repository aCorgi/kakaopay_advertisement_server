package com.task.kakaopayadvertisementserver.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rabbitmq.client.Channel
import com.task.kakaopayadvertisementserver.client.RewardInternalApiClient
import com.task.kakaopayadvertisementserver.dto.api.PointEarningApiRequest
import com.task.kakaopayadvertisementserver.dto.message.AdvertisementParticipationCompletedMessageDto
import com.task.kakaopayadvertisementserver.util.logger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageQueueConsumer(
    private val objectMapper: ObjectMapper,
    private val mockRewardInternalApiClient: RewardInternalApiClient,
) {
    val log = logger<MessageQueueConsumer>()

    @RabbitListener(
        queues = ["\${rabbitmq.advertisement-participation-message-queue.name}"],
    )
    fun receiveAdvertisementParticipationCompletedMessage(
        message: String,
        channel: Channel,
    ) {
        val messageDto = objectMapper.readValue<AdvertisementParticipationCompletedMessageDto>(message)
        log.info("광고 참여 완료 메시지 수신: $messageDto")

        mockRewardInternalApiClient.earnPointByMemberId(
            PointEarningApiRequest(
                memberId = messageDto.memberId,
                point = messageDto.point,
            ),
        )
    }
}
