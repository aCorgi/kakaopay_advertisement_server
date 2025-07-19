package com.task.kakaopayadvertisementserver.dto.event

data class AdvertisementParticipationCompletedEvent(
    val memberId: Int,
    val point: Int,
)
