package one.ifelse.module.base.service.token

import org.springframework.data.repository.CrudRepository

interface AccessTokenRepository : CrudRepository<AccessToken, String>