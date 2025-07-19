package com.task.kakaopayadvertisementserver.component

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
object SpringContext : ApplicationContextAware {
    lateinit var applicationContext: ApplicationContext
        private set

    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }
}
