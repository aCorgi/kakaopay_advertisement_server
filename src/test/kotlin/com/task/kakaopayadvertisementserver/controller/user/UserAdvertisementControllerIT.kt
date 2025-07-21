package com.task.kakaopayadvertisementserver.controller.user

import com.task.kakaopayadvertisementserver.config.ControllerTestBase
import com.task.kakaopayadvertisementserver.config.WithMockKakaopayMember
import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime
import kotlin.test.Test

class UserAdvertisementControllerIT : ControllerTestBase() {
    companion object {
        const val MEMBER_ID = 122
    }

    @Nested
    inner class `참가할 수 있는 광고 목록 조회` {
        private val url = "/user/advertisements"

        @Nested
        inner class `성공` {
            @WithMockKakaopayMember(
                id = MEMBER_ID,
                kakaopayAuthorities = [KakaopayAuthority.USER],
            )
            @Test
            fun `광고 목록 조회에 성공하면 200 OK 와 광고 목록을 반환한다`() {
                // given
                val advertisements =
                    listOf(
                        MockAdvertisement.of(name = "광고1", rewardAmount = 1000),
                        MockAdvertisement.of(name = "광고2", rewardAmount = 500),
                    )
                val advertisementResponses = advertisements.map { AdvertisementResponse(it) }

                whenever(advertisementService.findEligibleAdvertisements(eq(MEMBER_ID), any<LocalDateTime>()))
                    .thenReturn(advertisementResponses)

                // when & then
                mockMvc.get(url)
                    .andExpect {
                        status { isOk() }
                        content {
                            json(objectMapper.writeValueAsString(advertisementResponses))
                        }
                    }
            }
        }

        @Nested
        inner class `실패` {
            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `유저 권한이 아니면 403 FORBIDDEN 를 반환한다`() {
                // when & then
                mockMvc.get(url)
                    .andExpect {
                        status { isForbidden() }
                    }
            }

            @Test
            fun `Basic auth token 이 없으면 401 Unauthorized 를 반환한다`() {
                mockMvc.get(url)
                    .andExpect {
                        status { isUnauthorized() }
                    }
            }
        }
    }
}
