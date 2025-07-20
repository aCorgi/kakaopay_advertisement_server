package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.domain.entity.Member
import com.task.kakaopayadvertisementserver.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun create(member: Member): Member {
        return memberRepository.save(member)
    }

    @Transactional
    fun initializeAdminAndUserIfNotExists() {
        // ADMIN 계정, USER 계정, 두 권한을 가진 슈퍼 계정을 initialize 시 생성한다
        // username: admin@kakaopay.com, password: admin1234
        val (adminEmail, adminPassword) = "admin@kakaopay.com" to "\$2a\$10\$O0tpFLdFBOl1hjJvraUn6uH8h1ywVKWCZZSRxP/KOvuD.VIxmzvW6"
        // username: user@kakaopay.com, password: user1234
        val (userEmail, userPassword) = "user@kakaopay.com" to "\$2a\$10\$MLF1Fxr9aohjB.BbM9GzhOmybSrYEmH11Rupwdrfylo9jQpbmfQ5K"
        // username: haewon@kakaopay.com, password: haewon1234
        val (superUserEmail, superUserPassword) = "haewon@kakaopay.som" to "\$2a\$10\$TzDEMXNyh7Cb5xRjOXyAq.mrfmb5IawQZXpY3jCJgsIuQr/7dOdJq"

        findByEmailOrNull(adminEmail)
            ?: create(
                Member(
                    email = adminEmail,
                    password = adminPassword,
                    authorities = setOf(KakaopayAuthority.ADMIN),
                ),
            )

        findByEmailOrNull(userEmail)
            ?: create(
                Member(
                    email = userEmail,
                    password = userPassword,
                    authorities = setOf(KakaopayAuthority.USER),
                ),
            )

        findByEmailOrNull(superUserEmail)
            ?: create(
                Member(
                    email = superUserEmail,
                    password = superUserPassword,
                    authorities = setOf(KakaopayAuthority.USER, KakaopayAuthority.ADMIN),
                ),
            )
    }

    fun findByEmailOrNull(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    fun findByIdOrNull(id: Int): Member? {
        return memberRepository.findByIdOrNull(id)
    }
}
