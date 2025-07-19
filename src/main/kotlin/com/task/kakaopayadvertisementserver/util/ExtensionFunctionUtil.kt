package com.task.kakaopayadvertisementserver.util

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

inline fun <reified T> logger() = LoggerFactory.getLogger(T::class.java)

fun <T> List<T>.paginate(pageable: Pageable): Page<T> {
    val start = pageable.offset.toInt()
    val end = minOf(start + pageable.pageSize, this.size)
    val content = if (start < this.size) {
        this.subList(start, end)
    } else {
        emptyList()
    }

    return PageImpl(content, pageable, this.size.toLong())
}
