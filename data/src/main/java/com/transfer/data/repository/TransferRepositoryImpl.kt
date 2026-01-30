package com.transfer.data.repository

import com.transfer.data.api.TransferApi
import com.transfer.data.mappers.toDomain
import com.transfer.domain.TransferRepository
import com.transfer.domain.model.Conversion
import com.transfer.domain.model.Currency
import javax.inject.Inject

class TransferRepositoryImpl @Inject constructor(
    private val api: TransferApi
) : TransferRepository {

    override suspend fun convert(from: Currency, to: Currency, amount: Double): Conversion {
        val dto = api.getFxRate(from.code, to.code, amount)
        return dto.toDomain()
    }
}