package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.Member
import com.task.kakaopayadvertisementserver.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun findByEmailOrNull(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    fun findByIdOrNull(id: Int): Member? {
        return memberRepository.findByIdOrNull(id)
    }
}
