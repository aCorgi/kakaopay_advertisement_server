package com.task.kakaopayadvertisementserver.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class TimestampBaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime")
    var createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime")
    var updatedAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
}
