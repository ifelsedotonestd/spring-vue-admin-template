package one.ifelse.module.base.service.user

import java.security.SecureRandom
import java.util.*

/**
 * @see [](https://mkyong.com/java/java-password-generator-example/>Ref source</a>
) */
object PasswordGenerator {

    private const val CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private val CHAR_UPPERCASE = CHAR_LOWERCASE.uppercase(Locale.getDefault())
    private const val DIGIT = "0123456789"
    private const val OTHER_PUNCTUATION = "!@#&()–[{}]:;',?/*"
    private const val OTHER_SYMBOL = "~$^+=<>"
    private const val OTHER_SPECIAL = OTHER_PUNCTUATION + OTHER_SYMBOL
    private val PASSWORD_ALLOW = CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + OTHER_SPECIAL
    private const val PASSWORD_LENGTH = 20
    private val random = SecureRandom()

    fun generateStrongPassword(): String {
        val result = StringBuilder(PASSWORD_LENGTH)

        // at least 2 chars (lowercase)
        val strLowerCase = generateRandomString(CHAR_LOWERCASE, 2)
        result.append(strLowerCase)

        // at least 2 chars (uppercase)
        val strUppercaseCase = generateRandomString(CHAR_UPPERCASE, 2)
        result.append(strUppercaseCase)

        // at least 2 digits
        val strDigit = generateRandomString(DIGIT, 2)
        result.append(strDigit)

        // at least 2 special characters (punctuation + symbols)
        val strSpecialChar = generateRandomString(OTHER_SPECIAL, 2)
        result.append(strSpecialChar)

        // remaining, just random
        val strOther = generateRandomString(PASSWORD_ALLOW, PASSWORD_LENGTH - 8)
        result.append(strOther)
        return result.toString()
    }

    // generate a random char[], based on `input`
    private fun generateRandomString(input: String?, size: Int): String {
        require(!input.isNullOrEmpty()) { "Invalid input." }
        require(size >= 1) { "Invalid size." }
        val result = StringBuilder(size)
        for (i in 0 until size) {
            // produce a random order
            val index = random.nextInt(input.length)
            result.append(input[index])
        }
        return result.toString()
    }
}
