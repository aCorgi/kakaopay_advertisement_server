package com.task.kakaopayadvertisementserver.client

import com.task.kakaopayadvertisementserver.dto.api.PointEarningApiRequest
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface RewardInternalApiClient {
    @PostExchange(EARN_POINT_BY_USER_ID_URL)
    fun earnPointByUserId(
        @RequestBody request: PointEarningApiRequest,
    )

    companion object {
        const val EARN_POINT_BY_USER_ID_URL = "/point"
    }
}
