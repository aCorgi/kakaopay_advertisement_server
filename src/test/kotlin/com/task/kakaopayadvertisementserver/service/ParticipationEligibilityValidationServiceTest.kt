package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.config.UnitTestBase
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibility
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibilityType
import com.task.kakaopayadvertisementserver.repository.AdvertisementParticipationRepository
import com.task.kakaopayadvertisementserver.repository.ParticipationEligibilityRepository
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockAdvertisementParticipation
import com.task.kakaopayadvertisementserver.util.MockMember
import org.junit.jupiter.api.Nested
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParticipationEligibilityValidationServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var participationEligibilityValidationService: ParticipationEligibilityValidationService

    @Mock
    private lateinit var participationEligibilityRepository: ParticipationEligibilityRepository

    @Mock
    private lateinit var advertisementParticipationRepository: AdvertisementParticipationRepository

    @Nested
    inner class `광고 참여 조건 검증` {
        @Test
        fun `FIRST_TIME 조건일 때 참여 이력이 없으면 true를 반환한다`() {
            // given
            val member = MockMember.of(id = 1)
            val advertisement = MockAdvertisement.of(id = 100)
            val eligibility =
                ParticipationEligibility(
                    advertisement = advertisement,
                    type = ParticipationEligibilityType.FIRST_TIME,
                    condition = null,
                )

            whenever(participationEligibilityRepository.findByAdvertisement(advertisement))
                .thenReturn(listOf(eligibility))
            whenever(advertisementParticipationRepository.findByMember(member))
                .thenReturn(emptyList())

            // when
            val result = participationEligibilityValidationService.isParticipationEligibleByAdvertisement(advertisement, member)

            // then
            assertTrue(result)
        }

        @Test
        fun `FIRST_TIME 조건일 때 참여 이력이 있으면 false를 반환한다`() {
            // given
            val member = MockMember.of(id = 1)
            val advertisement = MockAdvertisement.of(id = 100)
            val eligibility =
                ParticipationEligibility(
                    advertisement = advertisement,
                    type = ParticipationEligibilityType.FIRST_TIME,
                    condition = null,
                )
            val participation = MockAdvertisementParticipation.of(member = member, advertisement = advertisement)

            whenever(participationEligibilityRepository.findByAdvertisement(advertisement))
                .thenReturn(listOf(eligibility))
            whenever(advertisementParticipationRepository.findByMember(member))
                .thenReturn(listOf(participation))

            // when
            val result = participationEligibilityValidationService.isParticipationEligibleByAdvertisement(advertisement, member)

            // then
            assertFalse(result)
        }

        @Test
        fun `REPEATED 조건일 때 참여 이력이 N회 이상 있으면 true를 반환한다`() {
            // given
            val member = MockMember.of(id = 1)
            val (advertisement, anotherAdvertisement) = MockAdvertisement.of(id = 100) to MockAdvertisement.of(id = 10222)
            val eligibility =
                ParticipationEligibility(
                    advertisement = advertisement,
                    type = ParticipationEligibilityType.REPEATED,
                    condition = 2,
                )

            val participations =
                listOf(
                    MockAdvertisementParticipation.of(member = member, advertisement = advertisement),
                    MockAdvertisementParticipation.of(member = member, advertisement = anotherAdvertisement),
                )

            whenever(participationEligibilityRepository.findByAdvertisement(advertisement))
                .thenReturn(listOf(eligibility))
            whenever(advertisementParticipationRepository.findByMember(member))
                .thenReturn(participations)

            // when
            val result = participationEligibilityValidationService.isParticipationEligibleByAdvertisement(advertisement, member)

            // then
            assertTrue(result)
        }

        @Test
        fun `REPEATED 조건일 때 참여 이력이 조건 횟수 미만이면 false를 반환한다`() {
            // given
            val member = MockMember.of(id = 1)
            val advertisement = MockAdvertisement.of(id = 100)
            val eligibility =
                ParticipationEligibility(
                    advertisement = advertisement,
                    type = ParticipationEligibilityType.REPEATED,
                    condition = 3,
                )
            val participations =
                listOf(
                    MockAdvertisementParticipation.of(member = member, advertisement = advertisement),
                )

            whenever(participationEligibilityRepository.findByAdvertisement(advertisement))
                .thenReturn(listOf(eligibility))
            whenever(advertisementParticipationRepository.findByMember(member))
                .thenReturn(participations)

            // when
            val result = participationEligibilityValidationService.isParticipationEligibleByAdvertisement(advertisement, member)

            // then
            assertFalse(result)
        }

        @Test
        fun `HAS_PARTICIPATED_IN_ADVERTISEMENT 조건일 때 특정 광고 참여 이력이 있으면 true를 반환한다`() {
            // given
            val member = MockMember.of(id = 1)
            val advertisement = MockAdvertisement.of(id = 100)
            val otherAdvertisement = MockAdvertisement.of(id = 200)
            val eligibility =
                ParticipationEligibility(
                    advertisement = advertisement,
                    type = ParticipationEligibilityType.HAS_PARTICIPATED_IN_ADVERTISEMENT,
                    condition = otherAdvertisement.id,
                )
            val participation = MockAdvertisementParticipation.of(member = member, advertisement = otherAdvertisement)

            whenever(participationEligibilityRepository.findByAdvertisement(advertisement))
                .thenReturn(listOf(eligibility))
            whenever(advertisementParticipationRepository.findByMember(member))
                .thenReturn(listOf(participation))

            // when
            val result = participationEligibilityValidationService.isParticipationEligibleByAdvertisement(advertisement, member)

            // then
            assertTrue(result)
        }

        @Test
        fun `HAS_PARTICIPATED_IN_ADVERTISEMENT 조건일 때 특정 광고 참여 이력이 없으면 false를 반환한다`() {
            // given
            val member = MockMember.of(id = 1)
            val advertisement = MockAdvertisement.of(id = 100)
            val otherAdvertisement = MockAdvertisement.of(id = 200)
            val eligibility =
                ParticipationEligibility(
                    advertisement = advertisement,
                    type = ParticipationEligibilityType.HAS_PARTICIPATED_IN_ADVERTISEMENT,
                    condition = otherAdvertisement.id,
                )

            whenever(participationEligibilityRepository.findByAdvertisement(advertisement))
                .thenReturn(listOf(eligibility))
            whenever(advertisementParticipationRepository.findByMember(member))
                .thenReturn(emptyList())

            // when
            val result = participationEligibilityValidationService.isParticipationEligibleByAdvertisement(advertisement, member)

            // then
            assertFalse(result)
        }
    }
}
