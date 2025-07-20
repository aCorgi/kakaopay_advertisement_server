package com.task.kakaopayadvertisementserver.controller.user

import com.task.kakaopayadvertisementserver.config.security.KakaopayMember
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationResponse
import com.task.kakaopayadvertisementserver.service.AdvertisementParticipationService
import com.task.kakaopayadvertisementserver.util.Constants.Page.DEFAULT_PAGE
import com.task.kakaopayadvertisementserver.util.Constants.Page.MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Page
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/user/advertisement-participations")
@SecurityRequirement(name = "basicAuth")
class UserAdvertisementParticipationController(
    private val advertisementParticipationService: AdvertisementParticipationService,
) {
    @GetMapping
    fun findPagedAdvertisementParticipations(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") @Min(0) page: Int,
        @RequestParam(defaultValue = "$MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE")
        @Min(1)
        @Max(MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE.toLong())
        size: Int,
        @AuthenticationPrincipal kakaopayMember: KakaopayMember,
        @RequestParam startAt: LocalDateTime,
        @RequestParam endAt: LocalDateTime,
    ): Page<AdvertisementParticipationResponse> {
        return advertisementParticipationService.findPagedAdvertisementParticipations(
            page = page,
            size = size,
            memberId = kakaopayMember.id,
            startAt = startAt,
            endAt = endAt,
        )
    }

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
