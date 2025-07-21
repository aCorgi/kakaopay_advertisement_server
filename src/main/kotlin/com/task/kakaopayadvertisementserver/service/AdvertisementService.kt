package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.exception.ResourceNotFoundException
import com.task.kakaopayadvertisementserver.repository.AdvertisementRepository
import com.task.kakaopayadvertisementserver.util.Constants.MAX_ADVERTISEMENT_FETCH_COUNT
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class AdvertisementService(
    private val memberService: MemberService,
    private val advertisementRepository: AdvertisementRepository,
    private val participationEligibilityService: ParticipationEligibilityService,
    private val participationEligibilityValidationService: ParticipationEligibilityValidationService,
) {
    fun findByIdOrNull(id: Int): Advertisement? {
        return advertisementRepository.findByIdOrNull(id)
    }

    fun findEligibleAdvertisements(
        memberId: Int,
        nowAt: LocalDateTime,
    ): List<AdvertisementResponse> {
        val member =
            memberService.findByIdOrNull(memberId)
                ?: throw ResourceNotFoundException("존재하지 않는 회원입니다.")

        val advertisements = advertisementRepository.findAvailableAndVisibleAdvertisements(nowAt)
        val eligibleAdvertisements =
            advertisements.filter { advertisement ->
                participationEligibilityValidationService.isParticipationEligibleByAdvertisement(
                    advertisement = advertisement,
                    member = member,
                )
            }

        return eligibleAdvertisements
            .take(MAX_ADVERTISEMENT_FETCH_COUNT)
            .map { AdvertisementResponse(it) }
    }

    @Transactional
    fun createAdvertisement(request: AdvertisementCreationRequest) {
        advertisementRepository.findByName(request.name)
            ?.let {
                throw ClientBadRequestException("이미 동일한 광고명이 존재합니다. (요청 광고명: ${request.name})")
            }

        val advertisement = request.toEntity()

        advertisementRepository.save(advertisement)
        request.participationEligibilities.forEach {
            val participationEligibility = it.toEntity(advertisement)

            participationEligibilityService.create(participationEligibility)
        }
    }
}
