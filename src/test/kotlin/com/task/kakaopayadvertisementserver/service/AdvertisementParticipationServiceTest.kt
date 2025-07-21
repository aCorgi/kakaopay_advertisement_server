package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.config.UnitTestBase
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationResponse
import com.task.kakaopayadvertisementserver.dto.event.AdvertisementParticipationCompletedEvent
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.exception.ResourceNotFoundException
import com.task.kakaopayadvertisementserver.repository.AdvertisementParticipationRepository
import com.task.kakaopayadvertisementserver.util.Constants.Page.MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockAdvertisementParticipation
import com.task.kakaopayadvertisementserver.util.MockMember
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    @Mock
    private lateinit var lockService: LockService

    @Mock
    private lateinit var participationEligibilityValidationService: ParticipationEligibilityValidationService

    @Nested
    inner class `광고 참여 등록` {
        @Nested
        inner class `성공` {
            @Test
            fun `광고 참여 등록에 성공하여 트랜잭션 커밋 후 포인트 적립을 위한 이벤트를 발행한다`() {
                // given
                val member = MockMember.of(id = 1)
                val advertisement = MockAdvertisement.of(id = 100, name = "광고1", rewardAmount = 500)
                val request = AdvertisementParticipationRequest(advertisementId = advertisement.id)

                whenever(memberService.findByIdOrNull(member.id))
                    .thenReturn(member)
                whenever(advertisementService.findByIdOrNull(request.advertisementId))
                    .thenReturn(advertisement)
                whenever(
                    participationEligibilityValidationService.isParticipationEligibleByAdvertisement(
                        advertisement = advertisement,
                        member = member,
                    ),
                ).thenReturn(true)
                whenever(lockService.runWithLock(anyString(), any<() -> Unit>()))
                    .thenAnswer {
                        (it.arguments[1] as () -> Unit).invoke()
                    }

                // when & then
                assertDoesNotThrow {
                    advertisementParticipationService.participateAdvertisement(request, member.id)
                }

                verify(advertisementParticipationRepository)
                    .save(any())
                verify(eventPublisher)
                    .publishEvent(
                        AdvertisementParticipationCompletedEvent(
                            memberId = member.id,
                            point = advertisement.rewardAmount,
                        ),
                    )
            }
        }

        @Nested
        inner class `실패` {
            @Test
            fun `존재하지 않는 회원 ID로 광고 참여 등록 시 예외를 반환한다`() {
                // given
                val memberId = 999
                val request = AdvertisementParticipationRequest(advertisementId = 100)

                whenever(memberService.findByIdOrNull(memberId))
                    .thenReturn(null)

                // when & then
                assertThrows<ResourceNotFoundException> {
                    advertisementParticipationService.participateAdvertisement(request, memberId)
                }
            }

            @Test
            fun `존재하지 않는 광고 ID로 광고 참여 등록 시 예외를 반환한다`() {
                // given
                val member = MockMember.of(id = 1)
                val request = AdvertisementParticipationRequest(advertisementId = 999)

                whenever(memberService.findByIdOrNull(member.id))
                    .thenReturn(member)
                whenever(advertisementService.findByIdOrNull(request.advertisementId))
                    .thenReturn(null)

                // when & then
                assertThrows<ResourceNotFoundException> {
                    advertisementParticipationService.participateAdvertisement(request, member.id)
                }
            }

            @Test
            fun `참여 제한이 초과된 광고에 참여 시 예외를 반환한다`() {
                // given
                val member = MockMember.of(id = 1)
                val advertisement =
                    MockAdvertisement.of(
                        id = 100,
                        name = "광고1",
                        maxParticipationCount = 10,
                        currentParticipationCount = 10,
                    )
                val request = AdvertisementParticipationRequest(advertisementId = advertisement.id)

                whenever(memberService.findByIdOrNull(member.id)).thenReturn(member)
                whenever(advertisementService.findByIdOrNull(request.advertisementId)).thenReturn(advertisement)

                // when & then
                assertThrows<ClientBadRequestException> {
                    advertisementParticipationService.participateAdvertisement(request, member.id)
                }
            }

            @Test
            fun `이미 참여한 광고일 경우 예외를 반환한다`() {
                // given
                val member = MockMember.of(id = 1)
                val advertisement = MockAdvertisement.of(id = 100, name = "광고1")
                val request = AdvertisementParticipationRequest(advertisementId = advertisement.id)
                val existingParticipation = MockAdvertisementParticipation.of(member = member, advertisement = advertisement)

                whenever(memberService.findByIdOrNull(member.id))
                    .thenReturn(member)
                whenever(advertisementService.findByIdOrNull(request.advertisementId))
                    .thenReturn(advertisement)
                whenever(advertisementParticipationRepository.findByMemberAndAdvertisement(member, advertisement))
                    .thenReturn(existingParticipation)

                // when & then
                assertThrows<ClientBadRequestException> {
                    advertisementParticipationService.participateAdvertisement(request, member.id)
                }
            }

            @Test
            fun `다른 사용자가 광고 참여 중일 때 예외를 반환한다`() {
                // given
                val member = MockMember.of(id = 1)
                val advertisement = MockAdvertisement.of(id = 100, name = "광고1")
                val request = AdvertisementParticipationRequest(advertisementId = advertisement.id)

                whenever(memberService.findByIdOrNull(member.id)).thenReturn(member)
                whenever(advertisementService.findByIdOrNull(request.advertisementId)).thenReturn(advertisement)
                whenever(
                    participationEligibilityValidationService.isParticipationEligibleByAdvertisement(
                        advertisement = advertisement,
                        member = member,
                    ),
                ).thenReturn(true)
                whenever(lockService.runWithLock(anyString(), any<() -> Unit>()))
                    .thenReturn(null)

                // when & then
                assertThrows<ClientBadRequestException> {
                    advertisementParticipationService.participateAdvertisement(request, member.id)
                }
            }

            @Test
            fun `광고 참여 조건에 부합하지 않으면 예외를 반환한다`() {
                // given
                val member = MockMember.of(id = 1)
                val advertisement = MockAdvertisement.of(id = 100, name = "광고1")
                val request = AdvertisementParticipationRequest(advertisementId = advertisement.id)

                whenever(memberService.findByIdOrNull(member.id)).thenReturn(member)
                whenever(advertisementService.findByIdOrNull(request.advertisementId)).thenReturn(advertisement)
                whenever(
                    participationEligibilityValidationService.isParticipationEligibleByAdvertisement(
                        advertisement = advertisement,
                        member = member,
                    ),
                ).thenReturn(false)

                // when & then
                assertThrows<ClientBadRequestException> {
                    advertisementParticipationService.participateAdvertisement(request, member.id)
                }
            }
        }
    }

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
                val (startAt, endAt) = nowAt.minusDays(1) to nowAt.plusDays(2)
                val sort = Sort.by(Sort.Direction.ASC, AdvertisementParticipation::createdAt.name)
                val pageable = PageRequest.of(page, size, sort)

                val advertisementParticipations =
                    listOf(
                        MockAdvertisementParticipation.of(
                            member = member,
                            advertisement = advertisements[0],
                            createdAt = nowAt.minusDays(1),
                        ),
                        MockAdvertisementParticipation.of(
                            member = member,
                            advertisement = advertisements[1],
                            createdAt = nowAt.minusDays(2),
                        ),
                        MockAdvertisementParticipation.of(
                            member = member,
                            advertisement = advertisements[2],
                            createdAt = nowAt.minusDays(3),
                        ),
                    )
                val pagedAdvertisementParticipations =
                    PageImpl(
                        advertisementParticipations.sortedBy { it.createdAt },
                        pageable,
                        advertisementParticipations.size.toLong(),
                    )

                whenever(
                    advertisementParticipationRepository.findByMemberIdAndCreatedAtBetween(
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
                    it.assertThat(result.content[0]).isEqualTo(AdvertisementParticipationResponse(advertisementParticipations[2]))
                    it.assertThat(result.content[1]).isEqualTo(AdvertisementParticipationResponse(advertisementParticipations[1]))
                    it.assertThat(result.content[2]).isEqualTo(AdvertisementParticipationResponse(advertisementParticipations[0]))
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
                val startAt = nowAt.plusDays(2)
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
