package one.ifelse.module.base.exception

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import one.ifelse.module.base.helper.Response
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Handle default page error like 404, 500, etc.")
class ErrorWhiteLabelHandler : ErrorController {
    @RequestMapping(path = ["/error"])
    fun handlerError(request: HttpServletRequest): Any {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        if (status != null) {
            val statusCode = status.toString().toInt()
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return Response.failed(statusCode, "app.common.exception.http-not-found")
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return Response.failed(statusCode, "app.common.exception.unhandled")
            }
        }
        return Response.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "app.common.exception.unhandled")
    }
}
