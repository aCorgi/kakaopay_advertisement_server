package com.task.kakaopayadvertisementserver.config

import com.task.kakaopayadvertisementserver.client.RewardInternalApiClient
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.exception.InternalServerException
import com.task.kakaopayadvertisementserver.property.RewardInternalApiProperties
import com.task.kakaopayadvertisementserver.util.Constants.Exception.DEFAULT_SERVER_EXCEPTION_MESSAGE
import com.task.kakaopayadvertisementserver.util.logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

@Configuration
class RestClientConfiguration(
    private val rewardInternalApiProperties: RewardInternalApiProperties,
) {
    private val log = logger<RestClientConfiguration>()

    @Bean
    fun rewardInternalApiClient(): RewardInternalApiClient {
        val apiClient =
            createRestClient(rewardInternalApiProperties.baseUrl)
                .mutate()
                .defaultHeaders {
                    it.setBasicAuth(
                        rewardInternalApiProperties.username,
                        rewardInternalApiProperties.password,
                    )
                }
                .build()

        val factory =
            HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(apiClient))
                .build()

        return factory.createClient<RewardInternalApiClient>()
    }

    private fun createRestClient(baseUrl: String): RestClient {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders {
                it.contentType = MediaType.APPLICATION_JSON
            }
            .defaultStatusHandler(HttpStatusCode::is4xxClientError) { _, response ->
                val responseBodyBytes = response.body.readBytes()
                val apiResponseExceptionMessage = String(responseBodyBytes)

                throw ClientBadRequestException(apiResponseExceptionMessage)
            }
            .defaultStatusHandler(HttpStatusCode::is5xxServerError) { _, response ->
                val responseBodyBytes = response.body.readBytes()
                val apiResponseExceptionMessage = String(responseBodyBytes)

                log.error("Internal Api Error $apiResponseExceptionMessage")
                throw InternalServerException(DEFAULT_SERVER_EXCEPTION_MESSAGE)
            }
            .build()
    }
}
