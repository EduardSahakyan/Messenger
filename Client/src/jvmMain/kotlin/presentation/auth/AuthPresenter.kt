package presentation.auth

import data.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import presentation.base.BasePresenter

class AuthPresenter(
    override val presenterScope: CoroutineScope
): BasePresenter() {

    suspend fun connect(onConnected: () -> Unit) {
        var isConnected: Boolean
        do {
            isConnected = ChatRepository.tryConnect()
            delay(5000)
        } while (!isConnected)
        onConnected()
    }

}