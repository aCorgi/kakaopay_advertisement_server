package com.task.kakaopayadvertisementserver.config

import com.task.kakaopayadvertisementserver.config.security.KakaopayMember
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class MockKakaopayMemberSecurityContextFactory : WithSecurityContextFactory<WithMockKakaopayMember> {
    override fun createSecurityContext(annotation: WithMockKakaopayMember): SecurityContext {
        val kakaopayMember =
            KakaopayMember(
                id = annotation.id,
                email = annotation.email,
                roles = annotation.kakaopayAuthorities.toSet(),
            )

        val authorities =
            kakaopayMember.roles
                .map { SimpleGrantedAuthority(it.authority) }

        val authentication =
            UsernamePasswordAuthenticationToken(
                kakaopayMember,
                null,
                authorities,
            )

        return SecurityContextHolder.createEmptyContext()
            .apply { this.authentication = authentication }
    }
}
