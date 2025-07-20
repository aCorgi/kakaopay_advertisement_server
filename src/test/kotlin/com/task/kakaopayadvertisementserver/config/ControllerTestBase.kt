package com.task.kakaopayadvertisementserver.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.kakaopayadvertisementserver.KakaopayAdvertisementServerApplication
import com.task.kakaopayadvertisementserver.config.security.PasswordConfiguration
import com.task.kakaopayadvertisementserver.config.security.SecurityConfiguration
import com.task.kakaopayadvertisementserver.controller.admin.AdminAdvertisementController
import com.task.kakaopayadvertisementserver.controller.user.UserAdvertisementParticipationController
import com.task.kakaopayadvertisementserver.exception.ExceptionHandler
import com.task.kakaopayadvertisementserver.service.AdvertisementParticipationService
import com.task.kakaopayadvertisementserver.service.AdvertisementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(
    value = [
        AdminAdvertisementController::class,
        UserAdvertisementParticipationController::class,
    ],
)
@ActiveProfiles("test")
@ImportAutoConfiguration(
    exclude = [
        RedisAutoConfiguration::class,
        JpaRepositoriesAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class,
        DataSourceAutoConfiguration::class,
    ],
)
@ComponentScan(basePackages = ["com.task.kakaopayadvertisementserver.controller"])
@ContextConfiguration(
    classes = [
        KakaopayAdvertisementServerApplication::class,
        ObjectMapperConfiguration::class,
        ExceptionHandler::class,
        PasswordConfiguration::class,
        SecurityConfiguration::class,
        TestBasicAuthenticationProvider::class,
    ],
)
abstract class ControllerTestBase {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockitoBean
    protected lateinit var advertisementService: AdvertisementService

    @MockitoBean
    protected lateinit var advertisementParticipationService: AdvertisementParticipationService
}
