package data

import kotlinx.coroutines.delay
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
import kotlin.random.Random

object Repository {

    private var socket: Socket? = null
    private val id = UUID.randomUUID().toString()
    private val keyPair = RSAHelper.generateRSAKeyPair()
    private var sessionKey = ""

    fun connectToServer(host: String, port: Int): Boolean {
        return try {
            val connection = Socket(host, port)
            socket = connection
            true
        } catch (e: Exception) {
            false
        }
    }

    fun readData() : Flow<Message> {
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

    suspend fun sendMessage() {
        socket?.let {
            PrintWriter(it.getOutputStream(), true).println(Base64.getEncoder().encodeToString(keyPair.public.encoded))
            do {
                delay(5000)
                val message = generateMessage()
                val outputJson = Json.encodeToString<Message>(message)
                val output = PrintWriter(it.getOutputStream(), true)
                output.println(CaesarHelper.caesarEncrypt(outputJson, sessionKey))
            } while (true)
        }
    }

    private fun generateMessage() = Message(
        temperature = "${Random.nextInt(10, 25)}°C",
        windSpeed = "%.2f".format(Random.nextDouble(5.0, 24.0)).replace(",", ".").toDouble(),
        airQuality = if (Random.nextBoolean()) "Fair" else "Poor",
        sender = id,
        date = Calendar.getInstance().time.toString()
    )



}