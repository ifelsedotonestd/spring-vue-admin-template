@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package one.ifelse.module.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
