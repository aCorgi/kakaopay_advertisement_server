package com.task.kakaopayadvertisementserver.controller.user

import com.task.kakaopayadvertisementserver.config.security.KakaopayMember
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationRequest
import com.task.kakaopayadvertisementserver.service.AdvertisementParticipationService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/user/advertisement-participations")
@SecurityRequirement(name = "basicAuth")
class AdvertisementParticipationController(
    private val advertisementParticipationService: AdvertisementParticipationService,
) {
    @PostMapping
    fun participateAdvertisement(
        @RequestBody request: AdvertisementParticipationRequest,
        @AuthenticationPrincipal kakaopayMember: KakaopayMember,
    ) {
        advertisementParticipationService.participateAdvertisement(
            request = request,
            memberId = kakaopayMember.id,
        )
    }
}
