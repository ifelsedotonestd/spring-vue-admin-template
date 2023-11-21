package one.ifelse.module.base.handler.authen

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import one.ifelse.module.base.helper.RequestedAttribute
import one.ifelse.module.base.helper.Response
import one.ifelse.module.base.security.jwt.JwtToken
import one.ifelse.module.base.security.jwt.JwtTokenProvider
import org.springframework.web.bind.annotation.*

@Tag(name = "API provide authenticate, password reset and forgot flows.")
@RestController
@RequestMapping(path = ["/auth"])
class AuthenticationHandler(private val jwtTokenProvider: JwtTokenProvider) {
    @PostMapping(name = "Authorization entry point", path = ["/public/token"])
    @Operation(summary = "Login API, generate token if success")
    fun authorize(@Valid @RequestBody request: TokenRequestDto): Response<JwtToken> {
        val token = jwtTokenProvider.generateToken(request.username, request.password, request.rememberMe)
        return Response.ok(token, "app.auth.message.success")
    }

    @PostMapping(name = "Renew token", path = ["/token/renew"])
    @Operation(summary = "Regenerate token if refresh token is valid")
    fun reauthorize(@Valid @RequestBody request: RenewTokenRequestDto): Response<JwtToken> {
        val token: JwtToken = jwtTokenProvider.renewToken(request.refreshToken)
        return Response.ok(token)
    }


    @DeleteMapping(name = "Show token info", path = ["/token/revoke"])
    @Operation(summary = "Show the token information")
    fun revoke(@RequestAttribute(RequestedAttribute.BEARER_TOKEN_VALUE) jwt: String?): Response<String> {
        jwtTokenProvider.revokeToken(jwt!!)
        return Response.ok("app.auth.message.revoked")
    }
}
