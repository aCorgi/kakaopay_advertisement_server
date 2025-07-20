package com.task.kakaopayadvertisementserver.dto.message

import com.task.kakaopayadvertisementserver.dto.event.AdvertisementParticipationCompletedEvent

data class AdvertisementParticipationCompletedMessageDto(
    val memberId: Int,
    val point: Int,
) : RabbitMqMessageDto {
    constructor(event: AdvertisementParticipationCompletedEvent) : this(
        memberId = event.memberId,
        point = event.point,
    )
}
