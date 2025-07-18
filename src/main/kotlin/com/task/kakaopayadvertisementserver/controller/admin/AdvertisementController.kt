package com.task.kakaopayadvertisementserver.controller.admin

import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.service.AdvertisementService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/admin/advertisements")
@SecurityRequirement(name = "basicAuth")
class AdvertisementController(
    private val advertisementService: AdvertisementService,
) {
    @PostMapping
    fun createAdvertisement(
        @RequestBody @Valid request: AdvertisementCreationRequest,
    ) {
        advertisementService.createAdvertisement(request)
    }
}
