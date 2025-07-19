package com.task.kakaopayadvertisementserver.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.task.kakaopayadvertisementserver.component.SpringContext
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
open class JsonColumnConverter<T>(
    private val objectType: TypeReference<T>,
) : AttributeConverter<T, String> {
    private val objectMapper: ObjectMapper by lazy {
        SpringContext.applicationContext.getBean(ObjectMapper::class.java)
    }

    override fun convertToDatabaseColumn(contents: T): String {
        return objectMapper.writeValueAsString(contents)
    }

    override fun convertToEntityAttribute(dbData: String): T {
        return objectMapper.readValue(dbData, objectType)
    }
}
