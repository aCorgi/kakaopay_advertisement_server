package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
    fun findByName(name: String): Advertisement?

    fun findByExposureAtBetweenAndParticipationCountGreaterThanEqualOrderByRewardAmountDesc(
        pageable: Pageable,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
        participationCount: Int
    ): Page<Advertisement>
}
