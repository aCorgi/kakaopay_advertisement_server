package com.task.kakaopayadvertisementserver.aspect

import com.task.kakaopayadvertisementserver.util.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Service
class ControllerLogger {
    private val log = logger<ControllerLogger>()

    @Around("execution(* com.task.kakaopayadvertisementserver.controller..*Controller.*(..))")
    fun doLog(joinPoint: ProceedingJoinPoint): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        val servletRequestAttributes =
            RequestContextHolder.currentRequestAttributes() as? ServletRequestAttributes
                ?: throw TypeCastException()
        val payload =
            (joinPoint.signature as? MethodSignature ?: throw TypeCastException())
                .parameterNames
                .zip(joinPoint.args)
                .joinToString(prefix = "[", postfix = "]") { (parameter, value) -> "$parameter=$value" }

        try {
            log.info(
                """
                ${servletRequestAttributes.request.method} ${servletRequestAttributes.request.requestURI} ${joinPoint.signature.name}
                authentication: ${authentication?.name ?: "anonymous"}
                payload: $payload
                """.trimIndent(),
            )
            return joinPoint.proceed()
        } catch (exception: Exception) {
            log.error("", exception)
            throw exception
        }
    }
}
