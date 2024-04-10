package data

import app.ServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.security.interfaces.RSAPrivateKey
import java.util.*

object ChatRepository {

    private var socket: Socket? = null
    private val keyPair = RSAHelper.generateRSAKeyPair()
    private var sessionKey = ""

    fun receiveMessage(): Flow<Message> {
        return flow {
            socket?.let {
                val encryptedSessionKey = BufferedReader(InputStreamReader(it.inputStream)).readLine()
                val encryptedByteArray = Base64.getDecoder().decode(encryptedSessionKey)
                sessionKey = RSAHelper.decrypt(encryptedByteArray, keyPair.private as RSAPrivateKey)
                while (true) {
                    val input = BufferedReader(InputStreamReader(it.inputStream))
                    val encryptedMessage = input.readLine()
                    val messageJson = CaesarHelper.caesarDecrypt(encryptedMessage, sessionKey)
                    val message = Json.decodeFromString<Message>(messageJson)
                    emit(message)
                }
            }
        }
    }

    fun tryConnect(): Boolean {
        return try {
            val connection = Socket(ServerConfig.host, ServerConfig.port)
            socket = connection
            PrintWriter(connection.getOutputStream(), true).println(Base64.getEncoder().encodeToString(keyPair.public.encoded))
            true
        } catch (e: Exception) {
            false
        }
    }

}