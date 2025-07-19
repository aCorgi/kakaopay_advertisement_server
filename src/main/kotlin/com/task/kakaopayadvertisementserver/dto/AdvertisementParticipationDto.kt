package com.task.kakaopayadvertisementserver.dto

import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import java.time.LocalDateTime

data class AdvertisementParticipationRequest(
    val advertisementId: Int,
)

data class AdvertisementParticipationResponse(
    val participatedAt: LocalDateTime,
    val memberId: Int,
    val advertisementId: Int,
    val advertisementName: String,
    val rewardAmount: Int,
) {
    constructor(entity: AdvertisementParticipation) : this(
        participatedAt = entity.createdAt,
        memberId = entity.member.id,
        advertisementId = entity.advertisement.id,
        advertisementName = entity.advertisement.name,
        rewardAmount = entity.advertisement.rewardAmount,
    )
}
