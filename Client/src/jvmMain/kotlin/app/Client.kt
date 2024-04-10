package app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import presentation.auth.AuthScreen
import presentation.chat.ClientScreen
import presentation.dialogs.connectionerror.ConnectionErrorDialog

fun main() = application {
    var route: Screen by remember { mutableStateOf(Screen.AuthScreen) }
    when(route) {
        Screen.AuthScreen -> {
            Window(
                state = WindowState(size = DpSize(400.dp, 400.dp)),
                title = "Authorization",
                resizable = false,
                onCloseRequest = ::exitApplication
            ) {
                AuthScreen(
                    onAuth = {
                        route = Screen.ChatScreen
                    }
                )
            }
        }
        Screen.ChatScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                resizable = false,
                title = "Client"
            ) {
                ClientScreen(
                    onConnectionReset = {
                        route = Screen.ConnectionErrorDialog
                    }
                )
            }
        }
        Screen.ConnectionErrorDialog -> {
            Dialog(
                onCloseRequest = {
                    route = Screen.AuthScreen
                },
                title = "Connection error",
                state = DialogState(size = DpSize(400.dp, 200.dp), position = WindowPosition(Alignment.Center)),
                resizable = false
            ) {
                ConnectionErrorDialog()
            }
        }
    }
}
