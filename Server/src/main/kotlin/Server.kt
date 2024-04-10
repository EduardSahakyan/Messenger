import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.concurrent.thread


fun main() {
    val synchronizedList = Collections.synchronizedList(mutableListOf<Pair<String, Socket>>())
    val serverSocket = ServerSocket(ServerConfig.PORT)
    while (true) {
        val connected = serverSocket.accept()
        println("Connected " + connected.port)
        val publicInput = BufferedReader(InputStreamReader(connected.inputStream)).readLine()
        val decode: ByteArray = Base64.getDecoder().decode(publicInput)
        val publicKey = getPublicKeyFromBytes(decode)
        val sessionKey = UUID.randomUUID().toString()
        println(sessionKey)
        val encryptedSessionKey = encrypt(sessionKey, publicKey as RSAPublicKey)
        PrintWriter(connected.getOutputStream(), true).println(Base64.getEncoder().encodeToString(encryptedSessionKey))
        synchronizedList.add(sessionKey to connected)
        thread {
            val socket = sessionKey to connected
            var disconnected = false
            while (!disconnected) {
                try {
                    val input = BufferedReader(InputStreamReader(socket.second.inputStream))
                    val message = input.readLine() ?: continue
                    val decryptedMessage = caesarDecrypt(message, socket.first)
                    for (s in synchronizedList) {
                        val output = PrintWriter(s.second.getOutputStream(), true)
                        output.println(caesarEncrypt(decryptedMessage, s.first))
                    }
                } catch (e: SocketException) {
                    disconnected = true
                    println("Disconnected " + socket.second.port)
                }
            }
            synchronizedList.remove(socket)
        }
    }
}

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

fun shiftChar(shiftChar: Char): Char {
    val shift = if (shiftChar.isLowerCase()) shiftChar - 'a' else shiftChar - 'A'
    return shift.toChar()
}

fun encrypt(message: String, publicKey: RSAPublicKey): ByteArray {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    return cipher.doFinal(message.toByteArray())
}

fun getPublicKeyFromBytes(encodedBytes: ByteArray): PublicKey {
    val keySpec = X509EncodedKeySpec(encodedBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}