package com.task.kakaopayadvertisementserver.controller.user

import com.task.kakaopayadvertisementserver.config.security.KakaopayMember
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.service.AdvertisementService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/user/advertisements")
@SecurityRequirement(name = "basicAuth")
class UserAdvertisementController(
    private val advertisementService: AdvertisementService,
) {
    @GetMapping
    fun findAvailableAndVisibleAdvertisements(
        @AuthenticationPrincipal kakaopayMember: KakaopayMember,
    ): List<AdvertisementResponse> {
        return advertisementService.findAvailableAndVisibleAdvertisements(
            memberId = kakaopayMember.id,
            nowAt = LocalDateTime.now(),
        )
    }
}
