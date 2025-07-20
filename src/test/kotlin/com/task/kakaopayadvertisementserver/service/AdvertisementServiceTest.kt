package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.config.UnitTestBase
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.repository.AdvertisementRepository
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockDto.getMockAdvertisementCreationRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.test.Test

class AdvertisementServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var advertisementService: AdvertisementService

    @Mock
    private lateinit var advertisementRepository: AdvertisementRepository

    @Nested
    inner class `광고 생성` {
        @Nested
        inner class `성공` {
            @Test
            fun `요청받은 형태의 광고를 DB 에 저장한다`() {
                // given
                val name = "광고명"
                val request = getMockAdvertisementCreationRequest(name = name)
                val advertisement = request.toEntity()

                whenever(advertisementRepository.findByName(request.name))
                    .thenReturn(null)
                whenever(advertisementRepository.save(any<Advertisement>()))
                    .thenReturn(advertisement)

                // when & then
                assertDoesNotThrow {
                    advertisementService.createAdvertisement(request)
                }

                val advertisementToSaveCaptor = argumentCaptor<Advertisement>()
                verify(advertisementRepository)
                    .save(advertisementToSaveCaptor.capture())

                val savedAdvertisement = advertisementToSaveCaptor.firstValue

                assertThat(savedAdvertisement.name).isEqualTo(name)
                assertThat(savedAdvertisement.rewardAmount).isEqualTo(request.rewardAmount)
                assertThat(savedAdvertisement.maxParticipationCount).isEqualTo(request.maxParticipationCount)
                assertThat(savedAdvertisement.text).isEqualTo(request.text)
                assertThat(savedAdvertisement.imageUrl).isEqualTo(request.imageUrl)
                assertThat(savedAdvertisement.exposureAt.startAt)
                    .isEqualTo(request.exposureAt.exposureStartAt)
                assertThat(savedAdvertisement.exposureAt.endAt)
                    .isEqualTo(request.exposureAt.exposureEndAt)
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `생성하려는 광고명이 이미 존재하는 경우 400 오류를 반환한다`() {
                // given
                val name = "중복된 광고명"
                val request = getMockAdvertisementCreationRequest(name = name)
                val existingAdvertisement = getMockAdvertisementCreationRequest(name = name).toEntity()

                whenever(advertisementRepository.findByName(request.name))
                    .thenReturn(existingAdvertisement)

                // when & then
                assertThrows<ClientBadRequestException> {
                    advertisementService.createAdvertisement(request)
                }

                verify(advertisementRepository, never())
                    .save(any())
            }
        }
    }

    @Nested
    inner class `유저 노출 광고 목록 조회` {
        @Nested
        inner class `성공` {
            @DisplayName("보상 금액 기준으로 내림차순 정렬된 광고를 최대 10개 목록 조회한다.")
            @Test
            fun `요청한 시간 기준으로 목록 조회한다`() {
                // given
                val nowAt = LocalDateTime.now()
                val advertisements =
                    listOf(
                        MockAdvertisement.of(name = "광고2", maxParticipationCount = 12, rewardAmount = 500),
                        MockAdvertisement.of(name = "광고1", maxParticipationCount = 20, rewardAmount = 200),
                    )
                whenever(advertisementRepository.findAvailableAndVisibleAdvertisements(nowAt))
                    .thenReturn(advertisements)

                // when
                val result =
                    assertDoesNotThrow {
                        advertisementService.findAvailableAndVisibleAdvertisements(nowAt)
                    }

                // then
                assertSoftly {
                    it.assertThat(result).hasSize(2)
                    it.assertThat(result[0]).isEqualTo(AdvertisementResponse(advertisements[0]))
                    it.assertThat(result[1]).isEqualTo(AdvertisementResponse(advertisements[1]))
                }
            }
        }
    }
}
