package com.task.kakaopayadvertisementserver.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : TimestampBaseEntity() {
    @Id
    @Column(nullable = false, length = 11, columnDefinition = "int", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
}
