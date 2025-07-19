package com.task.kakaopayadvertisementserver.component

import com.task.kakaopayadvertisementserver.dto.event.AdvertisementParticipationCompletedEvent
import com.task.kakaopayadvertisementserver.util.logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AdvertisementParticipationEventHandler(
//    private val rabbitTemplate: RabbitTemplate
) {
    val log = logger<AdvertisementParticipationEventHandler>()

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAdvertisementParticipationCompleted(event: AdvertisementParticipationCompletedEvent) {
        log.info("광고 참여 완료 후 이벤트 처리 시작: $event")

        // TODO: exception 발생 시 noti 필요 (슬랙 등)
        // TODO: MQ 처리 필요
//        rabbitTemplate.convertAndSend(
//            "participation.exchange",
//            "participation.key",
//            event
//        )
    }
}
