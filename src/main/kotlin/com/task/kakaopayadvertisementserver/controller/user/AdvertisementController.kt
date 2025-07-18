package com.task.kakaopayadvertisementserver.controller.user

import com.task.kakaopayadvertisementserver.service.AdvertisementService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/user/advertisements")
@SecurityRequirement(name = "basicAuth")
class AdvertisementController(
    private val advertisementService: AdvertisementService,
)
