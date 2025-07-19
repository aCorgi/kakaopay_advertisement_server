package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AdvertisementParticipationService(
    private val advertisementService: AdvertisementService,
) {
    @Transactional
    fun participateAdvertisement(
        request: AdvertisementParticipationRequest,
        userId: Int,
    ) {
    }
}
