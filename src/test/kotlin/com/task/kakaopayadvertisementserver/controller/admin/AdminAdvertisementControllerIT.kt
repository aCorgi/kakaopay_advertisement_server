package com.task.kakaopayadvertisementserver.controller.admin

import com.task.kakaopayadvertisementserver.config.ControllerTestBase
import com.task.kakaopayadvertisementserver.config.WithMockKakaopayMember
import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockDto.getMockAdvertisementCreationRequest
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import kotlin.test.Test

class AdminAdvertisementControllerIT : ControllerTestBase() {
    @Nested
    inner class `광고 등록` {
        private val url = "/admin/advertisements"

        @Nested
        inner class `성공` {
            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `광고 등록에 성공하면 201 CREATED 를 반환한다`() {
                // given
                val request = getMockAdvertisementCreationRequest()

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                }

                verify(advertisementService).createAdvertisement(request)
            }
        }

        @Nested
        inner class `실패` {
            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.USER])
            @Test
            fun `어드민 권한이 아니면 403 FORBIDDEN 를 반환한다`() {
                // given
                val request = getMockAdvertisementCreationRequest()

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isForbidden() }
                }
            }

            @Test
            fun `Basic auth token 이 없으면 401 Unauthorized 를 반환한다`() {
                // given
                val request = getMockAdvertisementCreationRequest()

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `유효하지 않은 요청 데이터로 광고 등록 시 400 BAD REQUEST 를 반환한다`() {
                // given
                val invalidRequest =
                    getMockAdvertisementCreationRequest(
                        name = "",
                        rewardAmount = -100,
                        maxParticipationCount = -1,
                    )

                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(invalidRequest)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
