package com.task.kakaopayadvertisementserver.component

import com.task.kakaopayadvertisementserver.service.MemberService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ApplicationInitializer(
    private val memberService: MemberService,
) {
    @PostConstruct
    fun initialize() {
        // ADMIN 계정, USER 계정을 initialize 시 생성한다
        memberService.initializeAdminAndUserIfNotExists()
    }
}
