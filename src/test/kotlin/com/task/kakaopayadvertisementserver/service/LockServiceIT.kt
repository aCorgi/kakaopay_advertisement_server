package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.config.IntegrationTestBase
import com.task.kakaopayadvertisementserver.util.Constants.Redis.LOCK_LEASE_TIME_IN_SECONDS
import com.task.kakaopayadvertisementserver.util.Constants.Redis.LOCK_WAIT_TIME_IN_SECONDS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.TimeUnit
import kotlin.test.Test

class LockServiceIT : IntegrationTestBase() {
    @Autowired
    private lateinit var lockService: LockService

    @BeforeEach
    fun setUp() {
        redissonClient.keys.deleteByPattern("*")
    }

    @Nested
    inner class `Lock 을 획득하면 파라미터로 받은 익명 함수를 실행한다` {
        @Test
        fun `Lock 을 획득하면 파라미터로 받은 익명 함수 실행`() {
            // given
            val lockName = "test-lock"
            val jobExecuted = mutableListOf<Boolean>()

            // when
            val result =
                lockService.runWithLock(lockName) {
                    jobExecuted.add(true)

                    "Job Result"
                }

            // then
            assertThat(result).isEqualTo("Job Result")
            assertThat(jobExecuted).hasSize(1)
            assertThat(jobExecuted[0]).isTrue
            assertThat(redissonClient.getLock(lockName).isLocked).isFalse
        }

        @DisplayName(
            """
            $LOCK_WAIT_TIME_IN_SECONDS 기간동안 기다리며 retry 한다.
            $LOCK_LEASE_TIME_IN_SECONDS 기간동안 lock ttl 이 걸린다.
        """,
        )
        @Test
        fun `Lock 을 획득하지 못하면 주기적으로 retry 하는데, 그래도 획득하지 못하면 null 을 반환한다`() {
            // given
            val lockName = "test-lock"
            val lock = redissonClient.getLock(lockName)
            lock.tryLock(LOCK_WAIT_TIME_IN_SECONDS, LOCK_LEASE_TIME_IN_SECONDS, TimeUnit.SECONDS) // Simulate lock already acquired

            // when
            val result =
                lockService.runWithLock(lockName) {
                    "Job Result"
                }

            // then
            assertThat(result).isNull()
            lock.unlock()
        }
    }
}
