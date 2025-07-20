package com.task.kakaopayadvertisementserver.util

import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.domain.BaseEntity
import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.domain.entity.Member
import com.task.kakaopayadvertisementserver.util.Random.createRandomPositiveInteger
import jakarta.persistence.EntityManager
import java.time.LocalDateTime

object MockAdvertisement {
    fun of(
        id: Int = 0,
        name: String = "카카오페이 광고",
        rewardAmount: Int = createRandomPositiveInteger(),
        maxParticipationCount: Int = createRandomPositiveInteger(),
        currentParticipationCount: Int = 0,
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
            currentParticipationCount = currentParticipationCount,
            text = text,
            imageUrl = imageUrl,
            exposureAt = exposureAt,
        )
            .apply {
                this.id = id
            }
    }

    fun create(
        entityManager: EntityManager,
        advertisement: Advertisement = of(),
    ) {
        entityManager.save(advertisement)
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

    fun create(
        entityManager: EntityManager,
        member: Member = of(),
    ) {
        entityManager.save(member)
    }
}

object MockAdvertisementParticipation {
    fun of(
        id: Int = 0,
        advertisement: Advertisement = MockAdvertisement.of(),
        member: Member = MockMember.of(),
        createdAt: LocalDateTime = LocalDateTime.now(),
    ): AdvertisementParticipation {
        return AdvertisementParticipation(
            advertisement = advertisement,
            member = member,
        )
            .apply {
                this.id = id
                this.createdAt = createdAt
            }
    }

    fun createWith(
        entityManager: EntityManager,
        advertisementParticipation: AdvertisementParticipation = of(),
    ) {
        MockMember.create(entityManager, advertisementParticipation.member)
        MockAdvertisement.create(entityManager, advertisementParticipation.advertisement)

        entityManager.save(advertisementParticipation)
    }
}

inline fun <reified T : BaseEntity> EntityManager.save(entity: T) {
    if (entity.id == 0) {
        return this.persist(entity)
    }

    this.merge(entity)
}
