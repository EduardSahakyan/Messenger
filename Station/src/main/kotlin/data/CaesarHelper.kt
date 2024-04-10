package data

object CaesarHelper {

    fun caesarEncrypt(plaintext: String, key: String): String {
        return plaintext.mapIndexed { index, char ->
            char + shiftChar(key[index % key.length]).code
        }.joinToString("")
    }

    fun caesarDecrypt(ciphertext: String, key: String): String {
        return ciphertext.mapIndexed { index, char ->
            char - shiftChar(key[index % key.length]).code
        }.joinToString("")
    }

    private fun shiftChar(shiftChar: Char): Char {
        val shift = if (shiftChar.isLowerCase()) shiftChar - 'a' else shiftChar - 'A'
        return shift.toChar()
    }

}