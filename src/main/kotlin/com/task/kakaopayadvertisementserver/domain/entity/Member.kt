package com.task.kakaopayadvertisementserver.domain.entity

import com.fasterxml.jackson.core.type.TypeReference
import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.converter.JsonColumnConverter
import com.task.kakaopayadvertisementserver.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Converter
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "member_uk01", columnList = "email", unique = true),
    ],
)
class Member(
    @Column(nullable = false, length = 200)
    val email: String,
    @Column(nullable = false, length = 200)
    val password: String,
    @Column(nullable = false, columnDefinition = "JSON")
    @Convert(converter = KakaopayAuthoritySetConverter::class)
    var authorities: Set<KakaopayAuthority>,
) : BaseEntity()

@Converter(autoApply = true)
class KakaopayAuthoritySetConverter : JsonColumnConverter<Set<KakaopayAuthority>>(
    object : TypeReference<Set<KakaopayAuthority>>() {},
)
