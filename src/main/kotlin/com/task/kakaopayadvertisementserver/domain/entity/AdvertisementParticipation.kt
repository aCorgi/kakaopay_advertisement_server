package com.task.kakaopayadvertisementserver.domain.entity

import com.task.kakaopayadvertisementserver.domain.BaseEntity
import jakarta.persistence.ConstraintMode
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
        // TODO: 어필) 한 명의 사용자는 하나의 광고를 한 번만 참여할 수 있다.
        Index(name = "advertisement_participation_uk01", columnList = "advertisement_id, member_id", unique = true),
    ],
)
class AdvertisementParticipation(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id", foreignKey = ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    val advertisement: Advertisement,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    val member: Member,
) : BaseEntity()
