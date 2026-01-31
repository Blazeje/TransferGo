package com.transfer.transfergo

import com.transfer.domain.TransferRepository
import com.transfer.transfergo.ui.MviViewModel
import com.transfer.transfergo.TransferConract.State
import com.transfer.transfergo.TransferConract.Event
import com.transfer.transfergo.TransferConract.Effect
import com.transfer.transfergo.TransferConract.InternalEvent
import com.transfer.transfergo.fundation.CoroutineDispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val repository: TransferRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : MviViewModel<State, Event, InternalEvent, Effect>(
    initialState = State(),
    eventsDispatcher = dispatcherProvider.unconfined,
    uiDispatcher = dispatcherProvider.main,
) {

    override fun onHandleUiEvent(
        uiEvent: Event,
        state: State
    ) = when (uiEvent) {

        is Event.OnFromCurrencySelected -> {
            pushInternal(InternalEvent.Loading)
            refresh(
                state.copy(fromCurrency = uiEvent.currency)
            )
        }

        is Event.OnToCurrencySelected -> {
            pushInternal(InternalEvent.Loading)
            refresh(state.copy(toCurrency = uiEvent.currency))
        }

        is Event.OnAmountChanged -> {
            refresh(state.copy(amount = uiEvent.amount))
        }

        Event.OnSwapClicked -> {
            refresh(
                state.copy(
                    fromCurrency = state.toCurrency,
                    toCurrency = state.fromCurrency
                )
            )
        }
    }

    override fun reduce(
        event: InternalEvent,
        state: State
    ): State =
        when (event) {
            InternalEvent.Loading ->
                state.copy(loading = true)

            is InternalEvent.ConversionUpdated ->
                state.copy(
                    loading = false,
                    resultText = event.text
                )

            is InternalEvent.Error ->
                state.copy(loading = false)
        }

    private fun refresh(stateOverride: State? = null) {
        val currentState = stateOverride ?: state.value

        activeUiStateScope.launch {
            try {
                pushInternal(InternalEvent.Loading)

                val conversion = repository.convert(
                    from = currentState.fromCurrency,
                    to = currentState.toCurrency,
                    amount = currentState.amount
                )

                pushInternal(
                    InternalEvent.ConversionUpdated(
                        "${conversion.toAmount.amount} ${conversion.toAmount.currency} @ ${conversion.rate.rate}"
                    )
                )
            } catch (e: Exception) {
                emitEffect(
                    Effect.ShowError(
                        e.message ?: "Unknown error"
                    )
                )
                pushInternal(
                    InternalEvent.Error(e.message.orEmpty())
                )
            }
        }
    }
}


