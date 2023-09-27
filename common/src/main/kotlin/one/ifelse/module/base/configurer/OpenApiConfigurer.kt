package one.ifelse.module.base.configurer

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfigurer {
    @Bean
    fun defaultSwaggerConfig(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("API")
                    .description("Demo")
                    .version("v0.0.1")
                    .license(License().name("MIT").url("http://ifelse.one"))
            )
    }
}
