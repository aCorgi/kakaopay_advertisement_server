package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibility
import com.task.kakaopayadvertisementserver.repository.ParticipationEligibilityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ParticipationEligibilityService(
    private val participationEligibilityRepository: ParticipationEligibilityRepository,
) {
    fun findByAdvertisement(advertisement: Advertisement): List<ParticipationEligibility> {
        return participationEligibilityRepository.findByAdvertisement(advertisement)
    }

    @Transactional
    fun create(participationEligibility: ParticipationEligibility): ParticipationEligibility {
        return participationEligibilityRepository.save(participationEligibility)
    }
}
