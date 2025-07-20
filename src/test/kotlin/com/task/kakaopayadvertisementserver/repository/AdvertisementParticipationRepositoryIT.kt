package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.config.IntegrationTestBase
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockAdvertisementParticipation
import com.task.kakaopayadvertisementserver.util.MockMember
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import kotlin.test.assertEquals

class AdvertisementParticipationRepositoryIT : IntegrationTestBase() {
    @Autowired
    private lateinit var advertisementParticipationRepository: AdvertisementParticipationRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var advertisementRepository: AdvertisementRepository

    @BeforeEach
    @AfterEach
    fun setUp() {
        memberRepository.deleteAll()
        advertisementRepository.deleteAll()
        advertisementParticipationRepository.deleteAll()
    }

    @Nested
    inner class `광고 참여 페이징 조회` {
        @Nested
        inner class `성공` {
            @Test
            fun `맴버ID 와 같고 생성일시가 BETWEEN 조건에 부합한 광고 참여를 조회한다`() {
                // given
                val (member, anotherMember) = MockMember.of(email = "a@a.aaa") to MockMember.of(email = "b@b.bbb")
                val advertisements =
                    listOf(
                        MockAdvertisement.of(name = "광고2"),
                        MockAdvertisement.of(name = "광고3"),
                        MockAdvertisement.of(name = "광고44"),
                    )
                val now = LocalDateTime.now()
                val participation =
                    MockAdvertisementParticipation.of(
                        member = member,
                        advertisement = advertisements[0],
                        createdAt = now.minusDays(1),
                    )
                val outOfParticipation =
                    MockAdvertisementParticipation.of(
                        member = member,
                        advertisement = advertisements[1],
                    )
                val participationOfAnotherMember =
                    MockAdvertisementParticipation.of(
                        member = anotherMember,
                        advertisement = advertisements[2],
                        createdAt = now.minusDays(1),
                    )

                val pageable = PageRequest.of(0, 10)
                val startAt = now.minusDays(2)
                val endAt = now.plusDays(1)

                transactional {
                    MockAdvertisementParticipation.createWith(entityManager, participation)
                    MockAdvertisementParticipation.createWith(entityManager, outOfParticipation)
                    MockAdvertisementParticipation.createWith(entityManager, participationOfAnotherMember)

                    outOfParticipation.createdAt = now.minusDays(3)
                }

                // when
                val result =
                    advertisementParticipationRepository.findByMemberIdAndCreatedAtBetween(
                        pageable = pageable,
                        memberId = member.id,
                        startAt = startAt,
                        endAt = endAt,
                    )

                // then
                assertEquals(1, result.totalElements)
                assertEquals(participation, result.content[0])
            }
        }
    }
}
