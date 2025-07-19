package com.task.kakaopayadvertisementserver.util

import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import java.time.LocalDateTime

object MockEntity {
    object MockAdvertisement {
        fun of(
            name: String = "카카오페이 광고",
            rewardAmount: Int = Random.createRandomPositiveInteger(),
            participationCount: Int = Random.createRandomPositiveInteger(),
            text: String = "카카오페이 쵝ㄱ오!!",
            imageUrl: String = "https://example.com/image.jpg",
            exposureAt: ExposureAt =
                ExposureAt(
                    startAt = LocalDateTime.now().minusDays(100),
                    endAt = LocalDateTime.now().plusDays(100),
                ),
        ): Advertisement {
            return Advertisement(
                name = name,
                rewardAmount = rewardAmount,
                participationCount = participationCount,
                text = text,
                imageUrl = imageUrl,
                exposureAt = exposureAt,
            )
        }
    }
}
