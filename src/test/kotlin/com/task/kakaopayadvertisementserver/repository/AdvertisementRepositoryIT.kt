package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.config.IntegrationTestBase
import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.util.Constants.MAX_ADVERTISEMENT_FETCH_COUNT
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
    inner class `노출 가능하고 참여 가능한 광고 목록 조회` {
        @Nested
        inner class `성공` {
            @DisplayName(
                """
                현재시간 기준 노출 기간에 포함하고, 최대 참여 횟수까지 증가하지 않은 광고를 적립 금액 내림차순으로 조회한다
                최대 $MAX_ADVERTISEMENT_FETCH_COUNT 개까지 조회한다.
            """,
            )
            @Test
            fun `현재 시간 기준에 적합한 광고들을 조회한다`() {
                // given
                val nowAt = LocalDateTime.now()
                val advertisement =
                    MockAdvertisement.of(
                        name = "광고1",
                        rewardAmount = 1000,
                        maxParticipationCount = 10,
                        currentParticipationCount = 5,
                        exposureAt =
                            ExposureAt(
                                startAt = nowAt.minusDays(1),
                                endAt = nowAt.plusDays(1),
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
                                startAt = nowAt.minusDays(1),
                                endAt = nowAt.plusDays(1),
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
                                startAt = nowAt.minusDays(2),
                                endAt = nowAt.plusDays(2),
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
                                startAt = nowAt.minusDays(21),
                                endAt = nowAt.minusDays(15),
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
                    val result = advertisementRepository.findAvailableAndVisibleAdvertisements(nowAt)

                    // then
                    assertEquals(2, result.size)
                    assertEquals(otherAdvertisement.id, result[0].id)
                    assertEquals(otherAdvertisement.name, result[0].name)
                    assertEquals(advertisement.id, result[1].id)
                    assertEquals(advertisement.name, result[1].name)
                }
            }
        }
    }
}
