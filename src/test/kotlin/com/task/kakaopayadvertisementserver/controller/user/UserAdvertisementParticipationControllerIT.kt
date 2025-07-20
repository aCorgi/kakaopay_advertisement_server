package com.task.kakaopayadvertisementserver.controller.user

import com.task.kakaopayadvertisementserver.config.ControllerTestBase
import com.task.kakaopayadvertisementserver.config.WithMockKakaopayMember
import com.task.kakaopayadvertisementserver.config.security.KakaopayAuthority
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationResponse
import com.task.kakaopayadvertisementserver.util.Constants.Page.MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE
import com.task.kakaopayadvertisementserver.util.MockAdvertisementParticipation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

class UserAdvertisementParticipationControllerIT : ControllerTestBase() {
    companion object {
        const val MEMBER_ID = 122
    }

    @Nested
    inner class `광고 참여 등록` {
        private val url = "/user/advertisement-participations"

        @Nested
        inner class `성공` {
            @WithMockKakaopayMember(
                id = MEMBER_ID,
                kakaopayAuthorities = [KakaopayAuthority.USER],
            )
            @Test
            fun `광고 참여 등록에 성공하면 201 CREATED 를 반환한다`() {
                // given
                val request = AdvertisementParticipationRequest(advertisementId = 2)

                // when & then
                mockMvc.post(url) {
                    content = objectMapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }

                verify(advertisementParticipationService)
                    .participateAdvertisement(
                        request = request,
                        memberId = MEMBER_ID,
                    )
            }
        }

        @Nested
        inner class `실패` {
            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `어드민 권한으로 광고 참여 등록 시 403 FORBIDDEN 를 반환한다`() {
                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isForbidden() }
                }
            }

            @Test
            fun `Basic auth token 이 없으면 401 Unauthorized 를 반환한다`() {
                // when & then
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isUnauthorized() }
                }
            }
        }
    }

    @Nested
    inner class `광고 참여 목록 조회` {
        private val url = "/user/advertisement-participations"

        @Nested
        inner class `성공` {
            @WithMockKakaopayMember(
                id = MEMBER_ID,
                kakaopayAuthorities = [KakaopayAuthority.USER],
            )
            @Test
            fun `광고 참여 목록 조회에 성공하면 200 OK 와 참여 목록을 반환한다`() {
                // given
                val (page, size) = 0 to 10
                val createdAt = LocalDateTime.now()
                val (startAt, endAt) = createdAt.minusDays(2) to createdAt.plusDays(2)
                val sort = Sort.by(Sort.Direction.ASC, AdvertisementParticipation::createdAt.name)
                val pageable = PageRequest.of(page, size, sort)
                val participations =
                    listOf(
                        MockAdvertisementParticipation.of(id = 214, createdAt = createdAt),
                        MockAdvertisementParticipation.of(id = 55, createdAt = createdAt.minusDays(1)),
                    )
                val pageResponse =
                    PageImpl(participations, pageable, participations.size.toLong())
                        .map { AdvertisementParticipationResponse(it) }

                whenever(
                    advertisementParticipationService.findPagedAdvertisementParticipations(
                        page = page,
                        size = size,
                        memberId = MEMBER_ID,
                        startAt = startAt,
                        endAt = endAt,
                    ),
                )
                    .thenReturn(pageResponse)

                // when & then
                mockMvc.get(url) {
                    param("page", "$page")
                    param("size", "$size")
                    param("startAt", "$startAt")
                    param("endAt", "$endAt")
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
            private val startAt = LocalDateTime.now().minusDays(2)
            private val endAt = LocalDateTime.now().plusDays(2)

            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.ADMIN])
            @Test
            fun `어드민 권한으로 광고 참여 목록 조회 시 403 FORBIDDEN 를 반환한다`() {
                // when & then
                val (page, size) = 0 to 10
                mockMvc.get(url) {
                    param("page", "$page")
                    param("size", "$size")
                    param("startAt", "$startAt")
                    param("endAt", "$endAt")
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
                    param("startAt", "$startAt")
                    param("endAt", "$endAt")
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.USER])
            @Test
            fun `유효하지 않은 page 값으로 광고 참여 목록 조회 시 400 BAD REQUEST 를 반환한다`() {
                // when & then
                mockMvc.get(url) {
                    param("page", "-1")
                    param("size", "10")
                    param("startAt", "$startAt")
                    param("endAt", "$endAt")
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @WithMockKakaopayMember(kakaopayAuthorities = [KakaopayAuthority.USER])
            @ParameterizedTest
            @ValueSource(ints = [0, MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE + 1])
            fun `유효하지 않은 size 값으로 광고 참여 목록 조회 시 400 BAD REQUEST 를 반환한다`(size: Int) {
                // when & then
                mockMvc.get(url) {
                    param("page", "0")
                    param("size", "$size")
                    param("startAt", "$startAt")
                    param("endAt", "$endAt")
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
