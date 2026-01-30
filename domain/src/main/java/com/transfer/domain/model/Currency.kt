package com.transfer.domain.model

enum class Currency(val code: String, val limit: Double) {
    PLN("PLN", 20000.0),
    EUR("EUR", 5000.0),
    GBP("GBP", 1000.0),
    UAH("UAH", 50000.0);

    companion object {
        fun fromCode(code: String): Currency {
            return entries.first { it.code == code }
        }
    }
}
