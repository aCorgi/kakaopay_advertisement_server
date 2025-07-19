package com.task.kakaopayadvertisementserver.controller.admin

import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.service.AdvertisementService
import com.task.kakaopayadvertisementserver.util.Constants.Page.DEFAULT_PAGE
import com.task.kakaopayadvertisementserver.util.Constants.Page.MAX_ADVERTISEMENT_PAGE_SIZE
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/admin/advertisements")
@SecurityRequirement(name = "basicAuth")
class AdvertisementController(
    private val advertisementService: AdvertisementService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAdvertisement(
        @RequestBody @Valid request: AdvertisementCreationRequest,
    ) {
        advertisementService.createAdvertisement(request)
    }

    @GetMapping
    fun findPagedAdvertisements(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") @Min(0) page: Int,
        @RequestParam(defaultValue = "$MAX_ADVERTISEMENT_PAGE_SIZE") @Min(1) @Max(MAX_ADVERTISEMENT_PAGE_SIZE) size: Int,
    ) {
        advertisementService.findPagedAdvertisement(page, size, LocalDateTime.now())
    }
}
