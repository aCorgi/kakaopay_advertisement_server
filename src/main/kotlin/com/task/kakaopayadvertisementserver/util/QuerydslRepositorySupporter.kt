package com.task.kakaopayadvertisementserver.util

import com.querydsl.jpa.JPQLQuery
import com.task.kakaopayadvertisementserver.domain.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class QuerydslRepositorySupporter<T : BaseEntity>(
    entityClass: Class<T>,
) : QuerydslRepositorySupport(entityClass) {
    override fun getQuerydsl(): Querydsl {
        val querydsl = super.getQuerydsl()
        checkNotNull(querydsl) { "Querydsl is null" }
        return querydsl
    }

    fun <R> getPageByQuery(
        query: JPQLQuery<R>,
        pageable: Pageable,
    ): Page<R> {
        val results =
            querydsl.applyPagination(pageable, query)
                .limit(pageable.pageSize.toLong())
                .offset(pageable.offset)
                .fetch()

        return PageImpl(results, pageable, query.fetchCount())
    }
}
