package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.repository.AdvertisementRepository
import com.task.kakaopayadvertisementserver.util.Constants.MAX_PAGE_SIZE
import com.task.kakaopayadvertisementserver.util.Constants.MIN_PARTICIPATION_COUNT
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
) {
    fun findPagedAdvertisements(
        page: Int,
        size: Int,
        nowAt: LocalDateTime,
    ): Page<AdvertisementResponse> {
        val pageable = PageRequest.of(page, size)

        if (size > MAX_PAGE_SIZE) {
            throw ClientBadRequestException("페이지 사이즈는 최대 $MAX_PAGE_SIZE 까지 허용됩니다. (요청 페이지 사이즈: $size)")
        }

        // TODO: 참가 가능한 광고 여 부 (선택사항) 대응
        val pagedAdvertisements =
            advertisementRepository.findByExposureAtBetweenAndParticipationCountGreaterThanEqualOrderByRewardAmountDesc(
                pageable = pageable,
                startAt = nowAt,
                endAt = nowAt,
                participationCount = MIN_PARTICIPATION_COUNT,
            )

        return pagedAdvertisements.map { AdvertisementResponse(it) }
    }

    @Transactional
    fun createAdvertisement(request: AdvertisementCreationRequest) {
        advertisementRepository.findByName(request.name)
            ?.let {
                throw ClientBadRequestException("이미 동일한 광고명이 존재합니다. (요청 광고명: ${request.name})")
            }

        val advertisement = request.toEntity()

        advertisementRepository.save(advertisement)
    }
}
