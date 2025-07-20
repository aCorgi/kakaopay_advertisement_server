package com.task.kakaopayadvertisementserver.controller.admin

import com.task.kakaopayadvertisementserver.config.ControllerTestBase
import com.task.kakaopayadvertisementserver.config.WithMockKakaopayMember
import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.dto.AdvertisementResponse
import com.task.kakaopayadvertisementserver.util.Constants.Page.MAX_ADVERTISEMENT_PAGE_SIZE
import com.task.kakaopayadvertisementserver.util.MockAdvertisement
import com.task.kakaopayadvertisementserver.util.MockDto.getMockAdvertisementCreationRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import kotlin.test.Test

class AdvertisementControllerIT : ControllerTestBase() {
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

    @Nested
    inner class `광고 목록 조회` {
        private val url = "/admin/advertisements"

        @Nested
        inner class `성공` {
            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `광고 목록 조회에 성공하면 200 OK 와 광고 목록을 반환한다`() {
                // given
                val (page, size) = 0 to 10
                val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rewardAmount"))
                val advertisements =
                    listOf(
                        MockAdvertisement.of(name = "광고1", rewardAmount = 1000),
                        MockAdvertisement.of(name = "광고2", rewardAmount = 500),
                    )
                val pageResponse =
                    PageImpl(advertisements, pageable, advertisements.size.toLong())
                        .map { AdvertisementResponse(it) }

                whenever(advertisementService.findPagedAdvertisement(eq(page), eq(size), any<LocalDateTime>()))
                    .thenReturn(pageResponse)

                // when & then
                mockMvc.get(url) {
                    param("page", "$page")
                    param("size", "$size")
                }.andExpect {
                    status { isOk() }
                    content {
                        json(objectMapper.writeValueAsString(pageResponse))
                    }
                }
            }
        }

        @Nested
        inner class `실패` {
            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.USER])
            @Test
            fun `어드민 권한이 아니면 403 FORBIDDEN 를 반환한다`() {
                // when & then
                val (page, size) = 0 to 10
                mockMvc.get(url) {
                    param("page", "$page")
                    param("size", "$size")
                }.andExpect {
                    status { isForbidden() }
                }
            }

            @Test
            fun `Basic auth token 이 없으면 401 Unauthorized 를 반환한다`() {
                val (page, size) = 0 to 10
                mockMvc.get(url) {
                    param("page", "$page")
                    param("size", "$size")
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `유효하지 않은 page 값으로 광고 목록 조회 시 400 BAD REQUEST 를 반환한다`() {
                // when & then
                mockMvc.get(url) {
                    param("page", "-1") // Invalid page
                    param("size", "0") // Invalid size
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @ParameterizedTest
            @ValueSource(ints = [0, MAX_ADVERTISEMENT_PAGE_SIZE + 1])
            fun `유효하지 않은 size 값으로 광고 목록 조회 시 400 BAD REQUEST 를 반환한다`(size: Int) {
                // when & then
                mockMvc.get(url) {
                    param("page", "0") // Invalid page
                    param("size", "$size") // Invalid size
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
