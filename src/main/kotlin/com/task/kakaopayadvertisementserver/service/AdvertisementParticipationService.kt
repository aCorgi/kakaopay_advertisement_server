package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.domain.entity.Advertisement
import com.task.kakaopayadvertisementserver.domain.entity.AdvertisementParticipation
import com.task.kakaopayadvertisementserver.domain.entity.Member
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationRequest
import com.task.kakaopayadvertisementserver.dto.AdvertisementParticipationResponse
import com.task.kakaopayadvertisementserver.dto.event.AdvertisementParticipationCompletedEvent
import com.task.kakaopayadvertisementserver.exception.ClientBadRequestException
import com.task.kakaopayadvertisementserver.exception.ResourceNotFoundException
import com.task.kakaopayadvertisementserver.repository.AdvertisementParticipationRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class AdvertisementParticipationService(
    private val advertisementParticipationRepository: AdvertisementParticipationRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val advertisementService: AdvertisementService,
    private val participationEligibilityValidationService: ParticipationEligibilityValidationService,
    private val memberService: MemberService,
    private val lockService: LockService,
) {
    fun findPagedAdvertisementParticipations(
        page: Int,
        size: Int,
        memberId: Int,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ): Page<AdvertisementParticipationResponse> {
        if (startAt.isAfter(endAt)) {
            throw ClientBadRequestException("시작 시간은 종료 시간보다 같거나 이전이어야 합니다. (시작 시간: $startAt, 종료 시간: $endAt)")
        }

        val sort = Sort.by(Sort.Direction.ASC, AdvertisementParticipation::createdAt.name)
        val pageable = PageRequest.of(page, size, sort)
        val pagedAdvertisementParticipation =
            advertisementParticipationRepository.findByMemberIdAndCreatedAtBetween(
                pageable = pageable,
                memberId = memberId,
                startAt = startAt,
                endAt = endAt,
            )

        return pagedAdvertisementParticipation.map { AdvertisementParticipationResponse(it) }
    }

    @Transactional
    fun participateAdvertisement(
        request: AdvertisementParticipationRequest,
        memberId: Int,
    ) {
        val member =
            memberService.findByIdOrNull(memberId)
                ?: throw ResourceNotFoundException("존재하지 않는 회원입니다.")
        val advertisement =
            advertisementService.findByIdOrNull(request.advertisementId)
                ?: throw ResourceNotFoundException("존재하지 않는 광고입니다.")

        advertisement.validateParticipationLimit()
        advertisementParticipationRepository.findByMemberAndAdvertisement(
            member = member,
            advertisement = advertisement,
        )
            ?.let {
                throw ClientBadRequestException("이미 참여한 광고입니다.")
            }

        if (
            participationEligibilityValidationService.isParticipationEligibleByAdvertisement(
                advertisement = advertisement,
                member = member,
            ).not()
        ) {
            throw ClientBadRequestException("광고 참여 조건에 부합하지 않습니다.")
        }

        lockService.runWithLock(
            lockName = "participate-advertisement:${request.advertisementId}",
        ) {
            participateAdvertisementWithLock(
                advertisement = advertisement,
                member = member,
            )
        }
            ?: throw ClientBadRequestException("이미 다른 사용자가 광고 참여 중입니다. 잠시 후 다시 시도해주세요.")
    }

    private fun participateAdvertisementWithLock(
        advertisement: Advertisement,
        member: Member,
    ) {
        val advertisementParticipation =
            AdvertisementParticipation(
                advertisement = advertisement,
                member = member,
            )

        advertisementParticipationRepository.save(advertisementParticipation)
        advertisement.increaseParticipationCount()

        eventPublisher.publishEvent(
            AdvertisementParticipationCompletedEvent(
                memberId = member.id,
                point = advertisement.rewardAmount,
            ),
        )
    }
}
