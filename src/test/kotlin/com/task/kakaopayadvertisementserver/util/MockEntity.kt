package com.task.kakaopayadvertisementserver.util

import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.domain.entity.Member
import com.task.kakaopayadvertisementserver.util.Random.createRandomPositiveInteger
import java.time.LocalDateTime

object MockAdvertisement {
    fun of(
        id: Int = 0,
        name: String = "카카오페이 광고",
        rewardAmount: Int = createRandomPositiveInteger(),
        maxParticipationCount: Int = createRandomPositiveInteger(),
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
            maxParticipationCount = maxParticipationCount,
            currentParticipationCount = 0,
            text = text,
            imageUrl = imageUrl,
            exposureAt = exposureAt,
        )
            .apply {
                this.id = id
            }
    }
}

object MockMember {
    fun of(
        id: Int = 0,
        email: String = "banner4@naver.com",
        password: String = "blabla",
        authorities: Set<KakaopayAuthority> = setOf(KakaopayAuthority.ADMIN, KakaopayAuthority.USER),
    ): Member {
        return Member(
            email = email,
            password = password,
            authorities = authorities.toSortedSet(),
        )
            .apply {
                this.id = id
            }
    }
}

object MockAdvertisementParticipation {
    fun of(
        advertisement: Advertisement = MockAdvertisement.of(),
        member: Member = MockMember.of(),
        createdAt: LocalDateTime = LocalDateTime.now(),
    ): AdvertisementParticipation {
        return AdvertisementParticipation(
            advertisement = advertisement,
            member = member,
        )
            .apply {
                this.createdAt = createdAt
            }
    }
}
