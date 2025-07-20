package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibility
import org.springframework.data.jpa.repository.JpaRepository

interface ParticipationEligibilityRepository : JpaRepository<ParticipationEligibility, Int> {
    fun findByAdvertisement(advertisement: Advertisement): List<ParticipationEligibility>
}
