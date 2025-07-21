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

        // TODO: 어필) Redisson 분산락 (광고ID 단위)
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

        /*
            TODO: 어필) 보상 트랜잭션 관리 대신 트랜잭션 커밋 성공까지 마친 후, 포인트 지급되도록 메세지큐 발행. 메세지 리스너가 포인트 지급 처리
            포인트 지급은 시간 차이가 발생할 수 있다.
            그러나, 포인트 지급 서버의 이상으로 광고 참여조차 못하게 두는 것은 사용자 관점에서 좋지 않다.
            MQ 로 포인트 지급 로깅 관리도 하면서, 미지급 시 retry 로 지급되도록 시스템이 자동으로 조치할 수 있다.
            그와 함께, 트래픽을 분산시킬 수 있다.
         */
        eventPublisher.publishEvent(
            AdvertisementParticipationCompletedEvent(
                memberId = member.id,
                point = advertisement.rewardAmount,
            ),
        )
    }
}
