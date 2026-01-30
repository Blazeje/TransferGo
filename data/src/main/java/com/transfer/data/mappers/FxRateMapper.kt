package com.transfer.data.mappers

import com.transfer.data.dto.FxRateDto
import com.transfer.domain.model.*

fun FxRateDto.toDomain(): Conversion {
    return Conversion(
        fromAmount = Money(
            amount = fromAmount,
            currency = Currency.fromCode(from)
        ),
        toAmount = Money(
            amount = toAmount,
            currency = Currency.fromCode(to)
        ),
        rate = FxRate(rate)
    )
}




