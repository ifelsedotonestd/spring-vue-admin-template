package one.ifelse.module.base.helper

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class StatusConverter : AttributeConverter<Status, Int> {
    override fun convertToDatabaseColumn(status: Status?): Int? {
        return status?.code
    }

    override fun convertToEntityAttribute(code: Int?): Status {
        return Status.of(code)
    }
}