package data

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import javax.crypto.Cipher

object RSAHelper {

    fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048) // You can change the key size here
        return keyPairGenerator.generateKeyPair()
    }

    fun decrypt(encryptedMessage: ByteArray, privateKey: RSAPrivateKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedMessage)
        return String(decryptedBytes)
    }

}