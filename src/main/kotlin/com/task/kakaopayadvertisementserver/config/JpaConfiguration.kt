package com.task.kakaopayadvertisementserver.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = ["com.task.kakaopayadvertisementserver.repository"])
@Configuration
class JpaConfiguration
