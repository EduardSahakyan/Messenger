package presentation.base

import kotlinx.coroutines.CoroutineScope

abstract class BasePresenter {
    protected abstract val presenterScope: CoroutineScope
}