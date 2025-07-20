package com.task.kakaopayadvertisementserver.config

import okhttp3.mockwebserver.MockWebServer

open class MockWebServerTestBase {
    companion object {
        val rewardInternalApiMockWebServer: MockWebServer = MockWebServer()

        init {
            rewardInternalApiMockWebServer.start()

            System.setProperty("api.reward-internal.base-url", "http://localhost:${rewardInternalApiMockWebServer.port}")
        }
    }
}
