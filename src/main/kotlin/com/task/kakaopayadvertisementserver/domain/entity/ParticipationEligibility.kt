package com.task.kakaopayadvertisementserver.domain.entity

import com.task.kakaopayadvertisementserver.converter.EnumColumnConverter
import com.task.kakaopayadvertisementserver.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Converter
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "participation_eligibility_uk01", columnList = "advertisement_id, type", unique = true),
    ],
)
class ParticipationEligibility(
    // TODO: 어필) 광고 특성 상 N 개의 참여 자격이 존재할 수 있다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id", foreignKey = ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    val advertisement: Advertisement,
    @Column(nullable = false)
    val type: ParticipationEligibilityType,
    @Column(nullable = true)
    val condition: Int?,
) : BaseEntity()

@Converter(autoApply = true)
class ParticipationEligibilityTypeConverter : EnumColumnConverter<ParticipationEligibilityType>(ParticipationEligibilityType::class.java)

enum class ParticipationEligibilityType(private val description: String) {
    HAS_PARTICIPATED_IN_ADVERTISEMENT("특정 광고를 참가한 이력이 있는 유저"),
    REPEATED("총 여러 번 참가했던 유저"),
    FIRST_TIME("처음으로 광고에 참여하는 유저"),
}
