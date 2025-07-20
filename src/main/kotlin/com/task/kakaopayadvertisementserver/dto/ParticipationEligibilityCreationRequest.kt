package com.task.kakaopayadvertisementserver.dto

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibility
import com.task.kakaopayadvertisementserver.domain.entity.ParticipationEligibilityType

data class ParticipationEligibilityCreationRequest(
    val type: ParticipationEligibilityType,
    val condition: Int? = null,
) {
    fun toEntity(advertisement: Advertisement): ParticipationEligibility {
        return ParticipationEligibility(
            advertisement = advertisement,
            type = type,
            condition = condition,
        )
    }
}
