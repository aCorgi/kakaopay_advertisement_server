package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.domain.entity.Member
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibility
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibilityType
import com.task.kakaopayadvertisementserver.repository.AdvertisementParticipationRepository
import com.task.kakaopayadvertisementserver.repository.ParticipationEligibilityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ParticipationEligibilityValidationService(
    private val participationEligibilityRepository: ParticipationEligibilityRepository,
    private val advertisementParticipationRepository: AdvertisementParticipationRepository,
) {
    fun isParticipationEligibleByAdvertisement(
        advertisement: Advertisement,
        member: Member,
    ): Boolean {
        val participationEligibilities = findByAdvertisement(advertisement)
        val advertisementParticipations = advertisementParticipationRepository.findByMember(member)

        return participationEligibilities.all { participationEligibility ->
            isParticipationEligible(advertisementParticipations, participationEligibility)
        }
    }

    private fun findByAdvertisement(advertisement: Advertisement): List<ParticipationEligibility> {
        return participationEligibilityRepository.findByAdvertisement(advertisement)
    }

    private fun isParticipationEligible(
        advertisementParticipations: List<AdvertisementParticipation>,
        participationEligibility: ParticipationEligibility,
    ): Boolean {
        return when (participationEligibility.type) {
            ParticipationEligibilityType.FIRST_TIME -> {
                advertisementParticipations.isEmpty()
            }
            ParticipationEligibilityType.REPEATED -> {
                advertisementParticipations.size >= (participationEligibility.condition ?: 0)
            }
            ParticipationEligibilityType.HAS_PARTICIPATED_IN_ADVERTISEMENT -> {
                advertisementParticipations.any { advertisementParticipation ->
                    advertisementParticipation.advertisement.id == participationEligibility.condition
                }
            }
        }
    }
}
