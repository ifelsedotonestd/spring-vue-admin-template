package one.ifelse.module.base.domain.token

import org.springframework.data.repository.CrudRepository

interface AccessTokenRepository : CrudRepository<AccessToken, String>