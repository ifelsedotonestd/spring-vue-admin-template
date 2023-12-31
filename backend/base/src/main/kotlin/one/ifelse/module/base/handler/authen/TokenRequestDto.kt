package one.ifelse.module.base.handler.authen

import jakarta.validation.constraints.NotNull

data class TokenRequestDto(
    @NotNull(message = "{app.auth.validation.username.required}") val username: String,
    @NotNull(message = "{app.auth.validation.password.required}") val password: String,
    val rememberMe: Boolean = false
)
