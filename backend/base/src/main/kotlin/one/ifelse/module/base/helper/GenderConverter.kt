package one.ifelse.module.base.helper

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class GenderConverter : AttributeConverter<Gender, Int> {
    override fun convertToDatabaseColumn(gender: Gender?): Int? {
        return gender?.code
    }

    override fun convertToEntityAttribute(code: Int?): Gender {
        return Gender.of(code)
    }
}