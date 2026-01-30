package com.transfer.domain.model

data class Conversion(
    val fromAmount: Money,
    val toAmount: Money,
    val rate: FxRate
)