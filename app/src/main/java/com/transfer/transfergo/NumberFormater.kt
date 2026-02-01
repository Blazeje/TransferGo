package com.transfer.transfergo

import java.util.Locale

object NumberFormater {

    fun amount(value: Double): String =
        "%.2f".format(Locale.US, value)

    fun rate(value: Double): String =
        "%.4f".format(Locale.US, value)
}
