package com.transfer.data

import com.transfer.data.api.TransferApi
import com.transfer.data.dto.FxRateDto
import com.transfer.data.repository.TransferRepositoryImpl
import com.transfer.domain.model.Currency
import com.transfer.domain.model.FxRate
import com.transfer.domain.model.Money
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test


class TransferRepositoryImplTest {

    private val api: TransferApi = mockk()
    private val sut = TransferRepositoryImpl(api)

    @Test
    fun `GIVEN successful API response, WHEN convert is called, THEN it should return mapped Conversion`() = runTest {
        val dto = FxRateDto(
            from = "PLN",
            to = "UAH",
            rate = 12.15,
            fromAmount = 300.0,
            toAmount = 3645.0
        )
        coEvery { api.getFxRate("PLN", "UAH", 300.0) } returns dto

        val result = runCatching {
            sut.convert(Currency.PLN, Currency.UAH, 300.0)
        }

        assertTrue(result.isSuccess)

        val conversion = result.getOrThrow()
        assertEquals(Money(300.0, Currency.PLN), conversion.fromAmount)
        assertEquals(Money(3645.0, Currency.UAH), conversion.toAmount)
        assertEquals(FxRate(12.15), conversion.rate)

        coVerify(exactly = 1) {
            api.getFxRate("PLN", "UAH", 300.0)
        }
    }


    @Test
    fun `GIVEN API throws exception, WHEN convert is called, THEN it should return failure`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { api.getFxRate(any(), any(), any()) } throws exception

        val result = runCatching {
            sut.convert(Currency.PLN, Currency.UAH, 300.0)
        }

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)

        coVerify(exactly = 1) {
            api.getFxRate("PLN", "UAH", 300.0)
        }
    }

}
