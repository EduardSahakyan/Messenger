import data.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun main(args: Array<String>) {
    runBlocking {
        val mainCoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        var isConnected = false
        do {
            isConnected = Repository.connectToServer(ServerConfig.HOST, ServerConfig.PORT)
            if (!isConnected) {
                println("Couldn't connect, trying again...")
                delay(5000)
            }
        } while (isConnected.not())
        println("Connected successfully")
        mainCoroutineScope.launch(Dispatchers.IO) {
            Repository.sendMessage()
        }
        Repository.readData()
            .flowOn(Dispatchers.IO)
            .onEach {
                println(it)
            }
            .flowOn(Dispatchers.Unconfined)
            .launchIn(mainCoroutineScope)
    }
    readln()
}