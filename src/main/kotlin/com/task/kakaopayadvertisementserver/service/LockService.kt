package com.task.kakaopayadvertisementserver.service

import com.task.kakaopayadvertisementserver.util.Constants.Redis.LOCK_LEASE_TIME_IN_SECONDS
import com.task.kakaopayadvertisementserver.util.Constants.Redis.LOCK_WAIT_TIME_IN_SECONDS
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class LockService(
    private val redissonClient: RedissonClient,
) {
    fun <T> runWithLock(
        lockName: String,
        job: () -> T,
    ): T? {
        val lock = redissonClient.getLock(lockName)
        val acquiredLock = lock.tryLock(LOCK_WAIT_TIME_IN_SECONDS, LOCK_LEASE_TIME_IN_SECONDS, TimeUnit.SECONDS)

        return if (acquiredLock) {
            try {
                job()
            } finally {
                lock.unlock()
            }
        } else {
            null
        }
    }
}
