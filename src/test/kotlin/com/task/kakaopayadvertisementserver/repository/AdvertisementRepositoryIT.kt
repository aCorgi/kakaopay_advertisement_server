package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.config.IntegrationTestBase
import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import kotlin.test.assertEquals

class AdvertisementRepositoryIT : IntegrationTestBase() {
    @Autowired
    private lateinit var advertisementRepository: AdvertisementRepository

    @BeforeEach
    @AfterEach
    fun setUp() {
        advertisementRepository.deleteAll()
    }

    @Nested
    inner class `광고 페이징 조회` {
        @Nested
        inner class `성공` {
            @DisplayName("현재시간 기준 노출 기간에 포함하고, 최대 참여 횟수까지 증가하지 않은 광고를 적립 금액 내림차순으로 조회한다")
            @Test
            fun `노출 가능하고 참여 가능한 광고를 페이징 조회한다`() {
                // given
                val now = LocalDateTime.now()
                val advertisement =
                    MockAdvertisement.of(
                        name = "광고1",
                        rewardAmount = 1000,
                        maxParticipationCount = 10,
                        currentParticipationCount = 5,
                        exposureAt =
                            ExposureAt(
                                startAt = now.minusDays(1),
                                endAt = now.plusDays(1),
                            ),
                    )
                val otherAdvertisement =
                    MockAdvertisement.of(
                        name = "광고5",
                        rewardAmount = 2000000,
                        maxParticipationCount = 10,
                        currentParticipationCount = 5,
                        exposureAt =
                            ExposureAt(
                                startAt = now.minusDays(1),
                                endAt = now.plusDays(1),
                            ),
                    )
                val fullyParticipatedAdvertisement =
                    MockAdvertisement.of(
                        name = "광고2",
                        rewardAmount = 500,
                        maxParticipationCount = 10,
                        currentParticipationCount = 10,
                        exposureAt =
                            ExposureAt(
                                startAt = now.minusDays(2),
                                endAt = now.plusDays(2),
                            ),
                    )
                val invisibleAdvertisement =
                    MockAdvertisement.of(
                        name = "광고3",
                        rewardAmount = 300,
                        maxParticipationCount = 5,
                        currentParticipationCount = 5,
                        exposureAt =
                            ExposureAt(
                                startAt = now.minusDays(21),
                                endAt = now.minusDays(15),
                            ),
                    )

                transactional {
                    MockAdvertisement.create(entityManager, advertisement)
                    MockAdvertisement.create(entityManager, otherAdvertisement)
                    MockAdvertisement.create(entityManager, fullyParticipatedAdvertisement)
                    MockAdvertisement.create(entityManager, invisibleAdvertisement)
                }

                // when & then
                transactional {
                    val sort = Sort.by(Sort.Direction.DESC, Advertisement::rewardAmount.name)
                    val pageable = PageRequest.of(0, 10, sort)
                    val result = advertisementRepository.findPagedAvailableAndVisibleAdvertisements(pageable, now)

                    // then
                    assertEquals(2, result.totalElements)
                    assertEquals(otherAdvertisement.id, result.content[0].id)
                    assertEquals(otherAdvertisement.name, result.content[0].name)
                    assertEquals(advertisement.id, result.content[1].id)
                    assertEquals(advertisement.name, result.content[1].name)
                }
            }
        }
    }
}
