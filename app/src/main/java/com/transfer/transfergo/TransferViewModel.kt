package com.transfer.transfergo

import com.transfer.domain.TransferRepository
import com.transfer.transfergo.TransferContract.Effect
import com.transfer.transfergo.TransferContract.Event
import com.transfer.transfergo.TransferContract.InternalEvent
import com.transfer.transfergo.TransferContract.State
import com.transfer.transfergo.fundation.CoroutineDispatcherProvider
import com.transfer.transfergo.ui.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val repository: TransferRepository,
    dispatcherProvider: CoroutineDispatcherProvider,
) : MviViewModel<State, Event, InternalEvent, Effect>(
    initialState = State(),
    eventsDispatcher = dispatcherProvider.unconfined,
    uiDispatcher = dispatcherProvider.main,
) {

    init {
        refresh(_state.value)
    }

    override fun onHandleUiEvent(
        uiEvent: Event,
        state: State,
    ) {
        when (uiEvent) {
            is Event.OnFromCurrencySelected ->
                refresh(state.copy(fromCurrency = uiEvent.currency))

            is Event.OnToCurrencySelected ->
                refresh(state.copy(toCurrency = uiEvent.currency))

            is Event.OnFromAmountChanged -> {
                val value = uiEvent.value
                val amount = value.toDoubleOrNull()
                val isTooLarge = amount?.let { it > state.fromCurrency.limit } ?: false

                refresh(
                    state.copy(
                        fromAmountInput = value,
                        error = isTooLarge,
                    ),
                )
            }

            is Event.OnToAmountChanged -> {
                val toAmount = uiEvent.value.toDoubleOrNull() ?: return
                if (state.rate == 0.0) return

                val fromAmount = toAmount / state.rate
                val isTooLarge = fromAmount > state.fromCurrency.limit

                refresh(
                    state.copy(
                        fromAmountInput = format(fromAmount),
                        error = isTooLarge,
                    ),
                )
            }

            Event.OnSwapClicked ->
                refresh(
                    state.copy(
                        fromCurrency = state.toCurrency,
                        toCurrency = state.fromCurrency,
                        fromAmountInput = state.toAmountInput.ifBlank { state.fromAmountInput },
                        error = false,
                    ),
                )

            is Event.OnNetworkErrorDismissed -> {
                _state.value = _state.value.copy(networkError = false)
            }
        }
    }

    override fun reduce(
        event: InternalEvent,
        state: State,
    ): State =
        when (event) {
            InternalEvent.Loading ->
                state.copy(loading = true)

            is InternalEvent.ConversionUpdated ->
                state.copy(
                    loading = false,
                    fromAmountInput = event.fromAmount,
                    toAmountInput = event.toAmount,
                    rate = event.rate,
                )

            is InternalEvent.Error ->
                state.copy(
                    loading = false,
                )
        }

    private fun refresh(stateOverride: State) {
        _state.value = stateOverride

        val amount = stateOverride.fromAmountInput.toDoubleOrNull() ?: return
        if (stateOverride.error) return

        activeUiStateScope.launch {
            try {
                pushInternal(InternalEvent.Loading)

                val conversion = repository.convert(
                    from = stateOverride.fromCurrency,
                    to = stateOverride.toCurrency,
                    amount = amount,
                )

                pushInternal(
                    InternalEvent.ConversionUpdated(
                        fromAmount = format(conversion.fromAmount.amount),
                        toAmount = format(conversion.toAmount.amount),
                        rate = conversion.rate.rate,
                    ),
                )
            } catch (e: IOException) {
                pushInternal(InternalEvent.Error(e.message.orEmpty()))
                _state.value = stateOverride.copy(networkError = true)
            } catch (e: Exception) {
                emitEffect(Effect.ShowError(e.message ?: "Unknown error"))
                pushInternal(InternalEvent.Error(e.message.orEmpty()))
            }
        }
    }
}

private fun format(value: Double): String =
    "%.2f".format(Locale.US, value)
