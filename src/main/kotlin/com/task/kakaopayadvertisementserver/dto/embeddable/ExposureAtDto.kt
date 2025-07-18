package com.task.kakaopayadvertisementserver.dto.embeddable

import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import java.time.LocalDateTime

data class ExposureAtDto(
    val exposureStartAt: LocalDateTime,
    val exposureEndAt: LocalDateTime,
) {
    fun toEmbeddable(): ExposureAt {
        return ExposureAt(
            startAt = exposureStartAt,
            endAt = exposureEndAt,
        )
    }
}
