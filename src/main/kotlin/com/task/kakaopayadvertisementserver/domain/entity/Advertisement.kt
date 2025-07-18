package com.task.kakaopayadvertisementserver.domain.entity

import com.task.kakaopayadvertisementserver.domain.BaseEntity
import com.task.kakaopayadvertisementserver.domain.embeddable.ExposureAt
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
    val participationCount: Int,
    @Column(nullable = false, length = 1000)
    val text: String,
    @Column(nullable = false, length = 500)
    val imageUrl: String,
    @Embedded
    val exposureAt: ExposureAt,
//    participationEligibility: ParticipationEligibility,
) : BaseEntity()

// @Converter(autoApply = true)
// class BookingStateConverter : EnumColumnConverter<ParticipationEligibility>(ParticipationEligibility::class.java)

// enum class ParticipationEligibility(private val description: String) {
//    HAS_HISTORY_WITH_ADVERTISEMENT(""),
//    REPEATED,
//    FIRST_TIME,
// }
