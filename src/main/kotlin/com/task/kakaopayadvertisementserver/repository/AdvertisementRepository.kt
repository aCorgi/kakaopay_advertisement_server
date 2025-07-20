package com.task.kakaopayadvertisementserver.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.QAdvertisement.advertisement
import com.task.kakaopayadvertisementserver.util.QuerydslRepositorySupporter
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AdvertisementRepository : JpaRepository<Advertisement, Int>, AdvertisementSearchRepository {
    fun findByName(name: String): Advertisement?
}

interface AdvertisementSearchRepository {
    fun findAvailableAndVisibleAdvertisements(nowAt: LocalDateTime): List<Advertisement>
}

class AdvertisementRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : QuerydslRepositorySupporter<Advertisement>(Advertisement::class.java), AdvertisementSearchRepository {
    override fun findAvailableAndVisibleAdvertisements(nowAt: LocalDateTime): List<Advertisement> {
        val query =
            jpaQueryFactory
                .selectFrom(advertisement)
                .where(
                    advertisement.exposureAt.startAt.loe(nowAt),
                    advertisement.exposureAt.endAt.goe(nowAt),
                    advertisement.maxParticipationCount.gt(advertisement.currentParticipationCount),
                )
                .orderBy(advertisement.rewardAmount.desc())

        return query.fetch()
    }
}
