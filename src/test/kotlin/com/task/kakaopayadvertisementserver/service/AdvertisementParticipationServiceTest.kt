package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.config.UnitTestBase
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationResponse
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.repository.AdvertisementParticipationRepository
import com.task.kakaopayadvertisementserver.util.Constants.Page.MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockAdvertisementParticipation
import com.task.kakaopayadvertisementserver.util.MockMember
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import kotlin.test.Test

class AdvertisementParticipationServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var advertisementParticipationService: AdvertisementParticipationService

    @Mock
    private lateinit var advertisementParticipationRepository: AdvertisementParticipationRepository

    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Mock
    private lateinit var advertisementService: AdvertisementService

    @Mock
    private lateinit var memberService: MemberService

    @Nested
    inner class `광고 참여 이력 페이징 조회` {
        @Nested
        inner class `성공` {
            @Test
            fun `요청받은 페이지와 사이즈로 광고 참여를 조회한다`() {
                // given
                val nowAt = LocalDateTime.now()
                val page = 0
                val size = MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE
                val member = MockMember.of(id = 1)
                val advertisements =
                    listOf(
                        MockAdvertisement.of(id = 13214, name = "광고1"),
                        MockAdvertisement.of(id = 2515, name = "광고2"),
                        MockAdvertisement.of(id = 2515333, name = "광고3"),
                    )
                val startAt = LocalDateTime.now().minusDays(1)
                val endAt = LocalDateTime.now()
                val pageable = PageRequest.of(page, size)

                val advertisementParticipations =
                    listOf(
                        MockAdvertisementParticipation.of(
                            member = member,
                            advertisement = advertisements[0],
                            createdAt = nowAt.minusDays(3),
                        ),
                        MockAdvertisementParticipation.of(
                            member = member,
                            advertisement = advertisements[1],
                            createdAt = nowAt.minusDays(2),
                        ),
                        MockAdvertisementParticipation.of(
                            member = member,
                            advertisement = advertisements[2],
                            createdAt = nowAt.minusDays(1),
                        ),
                    )
                val pagedAdvertisementParticipations = PageImpl(advertisementParticipations)

                whenever(
                    advertisementParticipationRepository.findByMemberIdAndCreatedAtBetweenOrderByCreatedAtAsc(
                        pageable = pageable,
                        memberId = member.id,
                        startAt = startAt,
                        endAt = endAt,
                    ),
                ).thenReturn(pagedAdvertisementParticipations)

                // when
                val result =
                    assertDoesNotThrow {
                        advertisementParticipationService.findPagedAdvertisementParticipations(
                            page = page,
                            size = size,
                            memberId = member.id,
                            startAt = startAt,
                            endAt = endAt,
                        )
                    }

                // then
                assertSoftly {
                    it.assertThat(result.content).hasSize(3)
                    it.assertThat(result.content[0]).isEqualTo(AdvertisementParticipationResponse(advertisementParticipations[0]))
                    it.assertThat(result.content[1]).isEqualTo(AdvertisementParticipationResponse(advertisementParticipations[1]))
                    it.assertThat(result.content[2]).isEqualTo(AdvertisementParticipationResponse(advertisementParticipations[2]))
                }
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `시작 시간이 종료 시간보다 이후인 경우 예외를 반환한다`() {
                // given
                val nowAt = LocalDateTime.now()
                val page = 0
                val size = MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE
                val memberId = 1
                val startAt = nowAt.plusDays(1)
                val endAt = nowAt.minusDays(1)

                // when & then
                assertThrows<ClientBadRequestException> {
                    advertisementParticipationService.findPagedAdvertisementParticipations(
                        page = page,
                        size = size,
                        memberId = memberId,
                        startAt = startAt,
                        endAt = endAt,
                    )
                }
            }
        }
    }
}
