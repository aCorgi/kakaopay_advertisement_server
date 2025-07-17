package com.task.kakaopayadvertisementserver.exception

import com.task.kakaopayadvertisementserver.util.Constants.Exception.DEFAULT_CLIENT_EXCEPTION_MESSAGE
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class ClientException(message: String = DEFAULT_CLIENT_EXCEPTION_MESSAGE) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ClientBadRequestException(message: String = "") : ClientException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(message: String = "") : ClientException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException(message: String = "") : ClientException(message)
