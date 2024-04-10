package presentation.chat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun ClientScreen(onConnectionReset: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()
    val presenter by remember { derivedStateOf { ChatPresenter(coroutineScope) } }
    val listState = rememberLazyListState()
    val state by presenter.state.collectAsState()

    LaunchedEffect("Client") {
        presenter.receiveMessage(onConnectionReset) {
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }

    MaterialTheme {
        Column {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.messages, key = { it.date }) {
                    MessageCard(it, Modifier)
                }
            }
        }

    }
}

