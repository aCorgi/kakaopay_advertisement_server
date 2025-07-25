package com.task.kakaopayadvertisementserver.dto

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.dto.embeddable.ExposureAtDto
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AdvertisementCreationRequest(
    @field:NotBlank
    val name: String,
    @field:Min(1)
    val rewardAmount: Int,
    @field:Min(1)
    val maxParticipationCount: Int,
    val text: String,
    val imageUrl: String,
    val exposureAt: ExposureAtDto,
    val participationEligibilities: List<ParticipationEligibilityCreationRequest>,
) {
    fun toEntity(): Advertisement {
        return Advertisement(
            name = name,
            rewardAmount = rewardAmount,
            maxParticipationCount = maxParticipationCount,
            currentParticipationCount = 0,
            text = text,
            imageUrl = imageUrl,
            exposureAt = exposureAt.toEmbeddable(),
        )
    }
}
