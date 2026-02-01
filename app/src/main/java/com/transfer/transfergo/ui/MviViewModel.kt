package com.transfer.transfergo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<STATE, EVENT, INTERNAL_EVENT, EFFECT>(
    initialState: STATE,
    private val eventsDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    protected val activeUiStateScope = viewModelScope

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<EFFECT>()

    fun push(event: EVENT) {
        viewModelScope.launch(eventsDispatcher) {
            onHandleUiEvent(event, _state.value)
        }
    }

    protected fun pushInternal(event: INTERNAL_EVENT) {
        _state.value = reduce(event, _state.value)
    }

    protected fun emitEffect(effect: EFFECT) {
        viewModelScope.launch(uiDispatcher) {
            _effects.emit(effect)
        }
    }

    protected abstract fun onHandleUiEvent(
        uiEvent: EVENT,
        state: STATE,
    )

    protected abstract fun reduce(
        event: INTERNAL_EVENT,
        state: STATE,
    ): STATE
}
