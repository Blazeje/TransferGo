package com.transfer.transfergo

import androidx.compose.runtime.Immutable
import com.transfer.domain.model.Currency

object TransferConract {

    @Immutable
    data class State(
        val loading: Boolean = false,
        val fromCurrency: Currency = Currency.PLN,
        val toCurrency: Currency = Currency.UAH,
        val amount: Double = 300.0,
        val resultText: String = ""
    )

    sealed interface Event {
        data class OnFromCurrencySelected(val currency: Currency) : Event
        data class OnToCurrencySelected(val currency: Currency) : Event
        data class OnAmountChanged(val amount: Double) : Event
        data object OnSwapClicked : Event
    }

    sealed interface InternalEvent {
        data object Loading : InternalEvent
        data class ConversionUpdated(val text: String) : InternalEvent
        data class Error(val message: String) : InternalEvent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
    }
}
