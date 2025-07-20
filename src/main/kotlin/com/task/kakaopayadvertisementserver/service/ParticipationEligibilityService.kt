package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibility
import com.task.kakaopayadvertisementserver.repository.ParticipationEligibilityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ParticipationEligibilityService(
    private val participationEligibilityRepository: ParticipationEligibilityRepository,
) {
    @Transactional
    fun create(participationEligibility: ParticipationEligibility): ParticipationEligibility {
        return participationEligibilityRepository.save(participationEligibility)
    }
}
