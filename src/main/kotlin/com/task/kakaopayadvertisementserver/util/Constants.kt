package com.task.kakaopayadvertisementserver.util

object Constants {
    object Exception {
        const val DEFAULT_CLIENT_EXCEPTION_MESSAGE = "잘못된 요청입니다."
        const val DEFAULT_SERVER_EXCEPTION_MESSAGE = "서버 연결이 원활하지 않습니다."
    }

    const val AES_ALGORITHM = "AES"

    object Page {
        const val MAX_ADVERTISEMENT_PARTICIPATION_PAGE_SIZE = 50
        const val DEFAULT_PAGE = 0L
    }

    const val MAX_ADVERTISEMENT_FETCH_COUNT = 10L

    object Redis {
        const val LOCK_WAIT_TIME_IN_SECONDS = 5L
        const val LOCK_LEASE_TIME_IN_SECONDS = 10L
    }
}
