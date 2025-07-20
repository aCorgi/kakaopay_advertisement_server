package com.task.kakaopayadvertisementserver.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val basicAuthenticationProvider: AuthenticationProvider,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { corsSpec -> corsSpec.disable() }
            .csrf { csrfSpec -> csrfSpec.disable() }
            .formLogin { formLoginSpec -> formLoginSpec.disable() }
            .logout { logoutSpec -> logoutSpec.disable() }
            .headers { headerSpec -> headerSpec.frameOptions { customizer -> customizer.disable() } }
            .authenticationProvider(basicAuthenticationProvider)
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(*permitUris())
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasAuthority(KakaopayMemberAuthority.ADMIN.authority)
                    .requestMatchers("/user/**")
                    .hasAuthority(KakaopayMemberAuthority.USER.authority)
                    .anyRequest()
                    .authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        return http.build()
    }

    fun permitUris(): Array<String> {
        return arrayOf(
            "/actuator/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
        )
    }
}
