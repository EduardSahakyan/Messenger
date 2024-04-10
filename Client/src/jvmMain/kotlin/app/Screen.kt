package app

sealed class Screen {
    object AuthScreen: Screen()
    object ChatScreen: Screen()
    object ConnectionErrorDialog: Screen()
}