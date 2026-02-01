package com.transfer.transfergo

import androidx.compose.runtime.Immutable
import com.transfer.domain.model.Currency

object TransferContract {

    @Immutable
    data class State(
        val loading: Boolean = false,
        val fromCurrency: Currency = Currency.PLN,
        val toCurrency: Currency = Currency.UAH,
        val fromAmountInput: String = "300.00",
        val toAmountInput: String = "",
        val rate: Double = 0.0,
        val error: Boolean = false,
        val networkError: Boolean = false,
    )

    sealed interface Event {
        data class OnFromCurrencySelected(val currency: Currency) : Event
        data class OnToCurrencySelected(val currency: Currency) : Event

        data class OnFromAmountChanged(val value: String) : Event
        data class OnToAmountChanged(val value: String) : Event

        data object OnSwapClicked : Event
        object OnNetworkErrorDismissed : Event
    }

    sealed interface InternalEvent {
        data object Loading : InternalEvent
        data class ConversionUpdated(
            val fromAmount: String,
            val toAmount: String,
            val rate: Double,
        ) : InternalEvent

        data class Error(val message: String) : InternalEvent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
    }
}
