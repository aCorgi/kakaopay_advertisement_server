package com.task.kakaopayadvertisementserver.util

import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.dto.embeddable.ExposureAtDto
import com.task.kakaopayadvertisementserver.util.Random.createRandomPositiveInteger
import java.time.LocalDateTime

object MockDto {
    fun getMockAdvertisementCreationRequest(
        name: String = "카카오페이 광고",
        rewardAmount: Int = createRandomPositiveInteger(),
        participationCount: Int = createRandomPositiveInteger(),
        text: String = "카카오페이 쵝ㄱ오!!",
        imageUrl: String = "https://example.com/image.jpg",
        exposureAt: ExposureAtDto =
            ExposureAtDto(
                exposureStartAt = LocalDateTime.now().minusDays(100),
                exposureEndAt = LocalDateTime.now().plusDays(100),
            ),
    ): AdvertisementCreationRequest {
        return AdvertisementCreationRequest(
            name = name,
            rewardAmount = rewardAmount,
            participationCount = participationCount,
            text = text,
            imageUrl = imageUrl,
            exposureAt = exposureAt,
        )
    }
}
