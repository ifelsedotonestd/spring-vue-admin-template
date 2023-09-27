package one.ifelse.module.base.helper

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import jakarta.servlet.ServletException
import one.ifelse.module.base.exception.JwtTokenException
import one.ifelse.module.base.exception.LogicException
import one.ifelse.module.base.helper.Translator.Companion.eval
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.*
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.Instant
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("status", "timestamp", "payload")
class Response<T>(
    val status: Int = 0,
    val timestamp: Long = 0,
    val message: String? = null,
    val payload: T? = null
) {


    data class ErrorContent(
        val code: String? = null,
        val details: Any? = null
    )

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        fun <T> builder(): ResponseBuilder<T> {
            return ResponseBuilder()
        }

        class ResponseBuilder<T> internal constructor() {
            private var status = 0
            private var timestamp: Long = 0
            private var message: String? = null
            private var payload: T? = null
            fun status(status: Int): ResponseBuilder<T> {
                this.status = status
                return this
            }

            fun timestamp(timestamp: Long): ResponseBuilder<T> {
                this.timestamp = timestamp
                return this
            }

            fun message(message: String?): ResponseBuilder<T> {
                this.message = message
                return this
            }

            fun payload(payload: T): ResponseBuilder<T> {
                this.payload = payload
                return this
            }

            fun build(): Response<T> {
                return Response(status, timestamp, message, payload)
            }

            override fun toString(): String {
                return "Response.ResponseBuilder(status=$status, timestamp=$timestamp, message=$message, payload=$payload)"
            }
        }

        fun <T> ok(): Response<T> {
            return builder<T>()
                .status(HttpStatus.OK.value())
                .message(eval("app.common.message.success"))
                .timestamp(now())
                .build()
        }

        private fun now(): Long {
            return Instant.now().epochSecond
        }

        fun <T> ok(payload: T): Response<T> {
            return builder<T>()
                .status(HttpStatus.OK.value())
                .message(eval("app.common.message.success"))
                .timestamp(now())
                .payload(payload)
                .build()
        }

        fun ok(message: String): Response<String> {
            return builder<String>()
                .status(HttpStatus.OK.value())
                .message(eval(message))
                .payload(eval(message))
                .timestamp(now())
                .build()
        }

        fun <T> ok(payload: T, message: String): Response<T> {
            return builder<T>()
                .status(HttpStatus.OK.value())
                .message(eval(message))
                .timestamp(now())
                .payload(payload)
                .build()
        }

        fun <T> ok(message: String, vararg args: Any?): Response<T> {
            return builder<T>()
                .status(HttpStatus.OK.value())
                .message(eval(message, args))
                .timestamp(now())
                .build()
        }

        fun failed(status: Int, message: String): Response<ErrorContent> {
            val uuid = UUID.randomUUID().toString()
            log(uuid, null)
            return builder<ErrorContent>()
                .status(status)
                .timestamp(now())
                .message(eval(message))
                .payload(ErrorContent(uuid, null))
                .build()
        }

        fun failed(e: LogicException): Response<*> = failed(HttpStatus.BAD_REQUEST.value(), e.message, e)

        private fun failed(httpCode: Int, message: String?, e: Throwable): Response<*> =
            failed(httpCode, message, null, e)

        private fun failed(httpCode: Int, message: String?, details: Any?, e: Throwable): Response<ErrorContent> {
            val uuid = UUID.randomUUID().toString()
            log(uuid, e)
            return builder<ErrorContent>()
                .status(httpCode)
                .timestamp(now())
                .message(message)
                .payload(ErrorContent(uuid, details))
                .build()
        }

        private fun log(uuid: String, e: Throwable?) {
            if (Objects.isNull(e)) {
                log.error(String.format("EXCEPTION CODE: %s ", uuid))
            } else if (e is Exception) {
                log.error(String.format("EXCEPTION CODE: %s ", uuid), e)
            } else if (e is Error) {
                log.error(String.format("ERROR CODE: %s", uuid), e)
            } else {
                log.error(String.format("THROWABLE CODE: %s ", uuid), e)
            }
        }

        fun failed(e: MethodArgumentNotValidException): Response<*> =
            failed(
                HttpStatus.BAD_REQUEST.value(),
                eval("app.common.exception.validation"),
                getFailedValidationFields(e),
                e
            )

        private fun getFailedValidationFields(ex: MethodArgumentNotValidException): Map<String, String?> {
            val errors: MutableMap<String, String?> = HashMap()
            ex.bindingResult.allErrors.forEach { error ->
                val fieldName: String = (error as FieldError).field
                val errorMessage: String? = error.getDefaultMessage()
                errors[fieldName] = errorMessage
            }
            return errors
        }

        fun failed(e: AccessDeniedException): Response<*> =
            failed(HttpStatus.FORBIDDEN.value(), eval("app.common.exception.forbidden-access"), e)

        fun failed(e: HttpRequestMethodNotSupportedException): Response<*> =
            failed(HttpStatus.NOT_ACCEPTABLE.value(), eval("app.common.exception.unsupported-method"), e)

        fun failed(e: HttpMediaTypeNotSupportedException): Response<*> =
            failed(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                eval("app.common.exception.unsupported-media-type"),
                e
            )

        fun failed(e: HttpMediaTypeNotAcceptableException): Response<*> =
            failed(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                eval("app.common.exception.unsupported-media-type"),
                e
            )

        fun failed(e: ServletException): Response<*> =
            failed(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), eval("app.common.exception.servlet"), e)

        fun failed(e: MissingServletRequestPartException): Response<*> =
            failed(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                eval("app.common.exception.missing-servlet-request-path"),
                e
            )

        fun failed(e: NoHandlerFoundException): Response<*> =
            failed(HttpStatus.NOT_FOUND.value(), eval("app.common.exception.not-found"), e)

        fun failed(e: HttpMessageConversionException): Response<*> =
            failed(HttpStatus.BAD_REQUEST.value(), eval("app.common.exception.http-message-conversion"), e)

        fun failed(e: BeansException): Response<*> =
            failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), eval("app.common.exception.property-access"), e)

        fun failed(e: AuthenticationException): Response<*> =
            failed(HttpStatus.UNAUTHORIZED.value(), getAuthenticationMessage(e), e)

        private fun getAuthenticationMessage(e: AuthenticationException): String {
            if (e is InsufficientAuthenticationException) {
                return eval("app.common.exception.authentication.insufficient")
            }
            if (e is AccountExpiredException) {
                return eval("app.common.exception.authentication.account-expired")
            }
            if (e is CredentialsExpiredException) {
                return eval("app.common.exception.authentication.account-credential-expired")
            }
            if (e is DisabledException) {
                return eval("app.common.exception.authentication.account-disabled")
            }
            if (e is LockedException) {
                return eval("app.common.exception.authentication.account-locked")
            }
            if (e is AccountStatusException) {
                return eval("app.common.exception.authentication.account-inaccessible")
            }
            if (e is BadCredentialsException) {
                return eval("app.common.exception.authentication.bad-credential")
            }
            if (e is JwtTokenException) {
                return eval("app.common.exception.authentication.invalid-jwt")
            }
            val message = e.message
            return eval(if (message!!.startsWith("app.")) message else "app.common.exception.authentication")
        }

        fun failed(e: DataAccessException): Response<*> =
            failed(HttpStatus.BAD_REQUEST.value(), eval("app.common.exception.cannot-access-data"), e)

        fun failed(e: RuntimeException): Response<*> =
            failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), eval("app.common.exception.runtime-unhandled"), e)

        fun failed(e: AsyncRequestTimeoutException): Response<*> =
            failed(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                eval("app.common.exception.async-request-timeout"),
                e
            )

        fun unexpected(e: Exception): Response<*> =
            failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), eval("app.common.exception.unhandled"), e)

        fun error(e: Error): Response<*> =
            failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), eval("app.common.exception.system"), e)
    }
}