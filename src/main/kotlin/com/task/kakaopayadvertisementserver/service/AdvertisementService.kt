package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.repository.AdvertisementRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
) {
    fun findByIdOrNull(id: Int): Advertisement? {
        return advertisementRepository.findByIdOrNull(id)
    }

    fun findAvailableAndVisibleAdvertisements(nowAt: LocalDateTime): List<AdvertisementResponse> {
        // TODO: 참가 가능한 광고 여 부 (선택사항) 대응
        val advertisements = advertisementRepository.findAvailableAndVisibleAdvertisements(nowAt)

        return advertisements.map { AdvertisementResponse(it) }
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
