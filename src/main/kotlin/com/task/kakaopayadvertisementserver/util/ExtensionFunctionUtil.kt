package com.task.kakaopayadvertisementserver.util

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

inline fun <reified T> logger() = LoggerFactory.getLogger(T::class.java)
