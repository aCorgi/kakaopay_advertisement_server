package com.task.kakaopayadvertisementserver.converter

import com.task.kakaopayadvertisementserver.exception.InternalServerException
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
open class EnumColumnConverter<T : Enum<T>>(private val enumClass: Class<T>) : AttributeConverter<T, String> {
    override fun convertToDatabaseColumn(contents: T): String {
        return contents.name
    }

    override fun convertToEntityAttribute(dbData: String): T {
        return enumClass.enumConstants.find {
            it.name == dbData
        } ?: throw InternalServerException("${enumClass.name}에 해당하는 enum 값이 아닙니다. (DB 데이터: $dbData)")
    }
}
