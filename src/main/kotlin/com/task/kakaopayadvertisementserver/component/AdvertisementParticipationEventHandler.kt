package com.task.kakaopayadvertisementserver.component

import com.task.kakaopayadvertisementserver.dto.event.AdvertisementParticipationCompletedEvent
import com.task.kakaopayadvertisementserver.dto.message.AdvertisementParticipationCompletedMessageDto
import com.task.kakaopayadvertisementserver.util.logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AdvertisementParticipationEventHandler(
    private val messageQueueProducer: MessageQueueProducer,
) {
    val log = logger<AdvertisementParticipationEventHandler>()

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAdvertisementParticipationCompleted(event: AdvertisementParticipationCompletedEvent) {
        log.info("광고 참여 완료 후 이벤트 처리 시작: $event")

        runCatching {
            messageQueueProducer.sendMessage(
                AdvertisementParticipationCompletedMessageDto(event),
            )
        }
            .onFailure {
                log.error("광고 참여 완료 후 포인트 적립 MQ producing 중 오류 발생: ${it.message}", it)

                // TODO: exception 발생 시 noti 필요 (slack, opsgenie on-call 등)
            }
    }
}
