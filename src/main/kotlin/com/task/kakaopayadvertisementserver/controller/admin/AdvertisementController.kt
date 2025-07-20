package com.task.kakaopayadvertisementserver.controller.admin

import com.task.kakaopayadvertisementserver.dto.AdvertisementCreationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.service.AdvertisementService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
    fun findPagedAdvertisements(): List<AdvertisementResponse> {
        return advertisementService.findAvailableAndVisibleAdvertisements(LocalDateTime.now())
    }
}
