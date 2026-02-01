package com.transfer.transfergo

import com.transfer.domain.TransferRepository
import com.transfer.domain.model.Conversion
import com.transfer.domain.model.Currency
import com.transfer.domain.model.FxRate
import com.transfer.domain.model.Money
import com.transfer.transfergo.fundation.CoroutineDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
class TransferViewModelTest {

    private val repository: TransferRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    init {
        Dispatchers.setMain(testDispatcher)

        coEvery { repository.convert(any(), any(), any()) } returns Conversion(
            fromAmount = Money(300.0, Currency.PLN),
            toAmount = Money(300.0, Currency.UAH),
            rate = FxRate(1.0),
        )
    }

    val dispatcherProvider = object : CoroutineDispatcherProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
        override val unconfined = testDispatcher
    }

    val sut = TransferViewModel(repository, dispatcherProvider)

    @Test
    fun `GIVEN initial state WHEN OnFromAmountChanged THEN fromAmountInput updates and triggers refresh`() = runTest {
        coEvery { repository.convert(Currency.PLN, Currency.UAH, 150.0) } returns Conversion(
            fromAmount = Money(150.0, Currency.PLN),
            toAmount = Money(300.0, Currency.UAH),
            rate = FxRate(2.0),
        )

        sut.push(TransferContract.Event.OnFromAmountChanged("150"))
        advanceUntilIdle()

        val state = sut.state.value
        assertEquals("150.00", state.fromAmountInput)
        assertEquals("300.00", state.toAmountInput)
        assertEquals(2.0, state.rate)
        assertFalse(state.error)
    }

    @Test
    fun `GIVEN amount exceeding limit WHEN OnFromAmountChanged THEN error is true and refresh is skipped`() = runTest {
        sut.push(TransferContract.Event.OnFromAmountChanged("20001"))
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state.error)
    }

    @Test
    fun `GIVEN state WHEN OnSwapClicked THEN currencies are swapped`() = runTest {
        val initialFrom = sut.state.value.fromCurrency
        val initialTo = sut.state.value.toCurrency

        sut.push(TransferContract.Event.OnSwapClicked)
        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(initialTo, state.fromCurrency)
        assertEquals(initialFrom, state.toCurrency)
    }

    @Test
    fun `GIVEN repository throws IOException WHEN conversion THEN networkError is true`() = runTest {
        coEvery { repository.convert(any(), any(), any()) } throws IOException("No connection")

        sut.push(TransferContract.Event.OnFromAmountChanged("100"))
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state.networkError)
        assertFalse(state.loading)
    }

    @Test
    fun `GIVEN OnFromCurrencySelected THEN fromCurrency updates`() = runTest {
        sut.push(TransferContract.Event.OnFromCurrencySelected(Currency.EUR))
        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(Currency.EUR, state.fromCurrency)
    }

    @Test
    fun `GIVEN OnToCurrencySelected THEN toCurrency updates`() = runTest {
        sut.push(TransferContract.Event.OnToCurrencySelected(Currency.GBP))
        advanceUntilIdle()

        val state = sut.state.value
        assertEquals(Currency.GBP, state.toCurrency)
    }

    @Test
    fun `GIVEN networkError is true WHEN OnNetworkErrorDismissed THEN networkError becomes false`() = runTest {
        coEvery { repository.convert(any(), any(), any()) } throws IOException()
        sut.push(TransferContract.Event.OnFromAmountChanged("100"))
        advanceUntilIdle()
        assertTrue(sut.state.value.networkError)

        sut.push(TransferContract.Event.OnNetworkErrorDismissed)
        advanceUntilIdle()

        assertFalse(sut.state.value.networkError)
    }
}
