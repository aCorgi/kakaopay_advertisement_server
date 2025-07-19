package com.task.kakaopayadvertisementserver.client

import com.task.kakaopayadvertisementserver.dto.api.PointEarningApiRequest
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@Component
@Primary
class MockRewardInternalApiClient : RewardInternalApiClient {
    override fun earnPointByUserId(request: PointEarningApiRequest) {
        // 적립 서버 요청에 대한 응답은 무시합니다.
        // Mock 호출을 위해 추가 구현이 없습니다.
    }
}

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
