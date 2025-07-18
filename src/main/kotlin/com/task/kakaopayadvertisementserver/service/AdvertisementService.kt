package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.repository.AdvertisementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
) {
    @Transactional
    fun createAdvertisement(request: AdvertisementCreationRequest) {
        val advertisement = request.toEntity()

        advertisementRepository.save(advertisement)
    }
}
