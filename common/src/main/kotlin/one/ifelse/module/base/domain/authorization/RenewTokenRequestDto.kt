package one.ifelse.module.base.domain.authorization

import jakarta.validation.constraints.NotNull

data class RenewTokenRequestDto(val refreshToken: @NotNull(message = "{app.auth.validation.refresh-token.required}") String)
