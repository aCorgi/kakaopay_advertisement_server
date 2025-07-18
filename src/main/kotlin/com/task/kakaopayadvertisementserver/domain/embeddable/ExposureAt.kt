package com.task.kakaopayadvertisementserver.domain.embeddable

import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class ExposureAt(
    @Column(name = "exposure_start_at", nullable = false, columnDefinition = "datetime")
    var startAt: LocalDateTime,
    @Column(name = "exposure_end_at", nullable = false, columnDefinition = "datetime")
    var endAt: LocalDateTime,
) {
    init {
        if (startAt.isAfter(endAt)) {
            throw ClientBadRequestException("노출 시작 일시가 종료 일시보다 미래입니다. (노출시작일시: $startAt, 노출종료일시: $endAt)")
        }
    }
}
