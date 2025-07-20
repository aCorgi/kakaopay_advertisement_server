package com.task.kakaopayadvertisementserver.config

import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = MockKakaopayMemberSecurityContextFactory::class)
annotation class WithMockKakaopayMember(
    val id: Int = 30000,
    val email: String = "banner4@naver.com",
    val kakaopayAuthorities: Array<KakaopayAuthority> = [],
)
