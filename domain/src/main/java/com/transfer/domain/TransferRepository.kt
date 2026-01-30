package com.transfer.domain

import com.transfer.domain.model.Conversion
import com.transfer.domain.model.Currency

interface TransferRepository {
    suspend fun convert(from: Currency, to: Currency, amount: Double): Conversion
}
