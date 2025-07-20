package com.task.kakaopayadvertisementserver.domain.entity

import com.task.kakaopayadvertisementserver.domain.BaseEntity
import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "advertisement_uk01", columnList = "name", unique = true),
    ],
)
class Advertisement(
    @Column(nullable = false, length = 200)
    val name: String,
    @Column(nullable = false)
    val rewardAmount: Int,
    @Column(nullable = false)
    val maxParticipationCount: Int,
    currentParticipationCount: Int,
    @Column(nullable = false, length = 1000)
    val text: String,
    @Column(nullable = false, length = 500)
    val imageUrl: String,
    @Embedded
    val exposureAt: ExposureAt,
) : BaseEntity() {
    @Column(nullable = false)
    var currentParticipationCount: Int = currentParticipationCount
        protected set

    fun increaseParticipationCount() {
        validateParticipationLimit()

        currentParticipationCount += 1
    }

    fun validateParticipationLimit() {
        if (maxParticipationCount <= currentParticipationCount) {
            throw ClientBadRequestException("참여 가능 횟수를 초과했습니다. (최대 참여 횟수: $maxParticipationCount, 현재 참여 횟수: $currentParticipationCount)")
        }
    }
}
