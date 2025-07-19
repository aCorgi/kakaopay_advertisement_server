package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AdvertisementParticipationRepository : JpaRepository<AdvertisementParticipation, Int> {
    fun findByMemberIdAndCreatedAtBetweenOrderByCreatedAtAsc(
        pageable: Pageable,
        memberId: Int,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ): Page<AdvertisementParticipation>
}
