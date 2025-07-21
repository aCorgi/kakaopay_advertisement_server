package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.domain.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AdvertisementParticipationRepository : JpaRepository<AdvertisementParticipation, Int> {
    fun findByMember(member: Member): List<AdvertisementParticipation>

    fun findByMemberAndAdvertisement(
        member: Member,
        advertisement: Advertisement,
    ): AdvertisementParticipation?

    fun findByMemberIdAndCreatedAtBetween(
        memberId: Int,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
        pageable: Pageable,
    ): Page<AdvertisementParticipation>
}
