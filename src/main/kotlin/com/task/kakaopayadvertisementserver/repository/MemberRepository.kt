package com.task.kakaopayadvertisementserver.repository

import com.task.kakaopayadvertisementserver.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Int> {
    fun findByEmail(email: String): Member?
}
