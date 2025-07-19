package com.task.kakaopayadvertisementserver.dto

import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement

data class AdvertisementResponse(
    val id: Int,
    val name: String,
    val text: String,
    val imageUrl: String,
    val exposureAt: ExposureAt,
    val rewardAmount: Int,
    val participationCount: Int,
) {
    constructor(entity: Advertisement): this(
        id = entity.id,
        name = entity.name,
        text = entity.text,
        imageUrl = entity.imageUrl,
        exposureAt = entity.exposureAt,
        rewardAmount = entity.rewardAmount,
        participationCount = entity.participationCount
    )
}