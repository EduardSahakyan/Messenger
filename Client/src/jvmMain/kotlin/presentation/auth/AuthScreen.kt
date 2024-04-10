package presentation.auth

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun AuthScreen(
    onAuth: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val presenter by remember { derivedStateOf { AuthPresenter(coroutineScope) } }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (isLoading.not()) {
            Button(onClick = {
                isLoading = isLoading.not()
                coroutineScope.launch(Dispatchers.IO) {
                    presenter.connect(onAuth)
                }
            }) {
                Text("Connect to server")
            }
        } else {
            CircularProgressIndicator()
        }
    }
}