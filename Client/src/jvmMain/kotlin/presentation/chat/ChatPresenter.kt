package presentation.chat

import data.ChatRepository
import data.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import presentation.base.BasePresenter

class ChatPresenter(
    override val presenterScope: CoroutineScope
): BasePresenter() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun receiveMessage(onConnectionReset: () -> Unit, onReceive: () -> Unit) {
        ChatRepository.receiveMessage()
            .flowOn(Dispatchers.IO)
            .catch {
                onConnectionReset()
            }
            .onEach { message ->
                val messages = state.value.messages.toMutableList()
                messages.add(0, message)
                _state.update { it.copy(messages = messages) }
                onReceive()
            }
            .flowOn(Dispatchers.Unconfined)
            .launchIn(presenterScope)
    }

    data class State(
        val messages: List<Message> = emptyList()
    )

}