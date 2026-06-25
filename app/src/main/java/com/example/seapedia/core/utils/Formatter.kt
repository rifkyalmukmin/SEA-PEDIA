package com.example.seapedia.core.utils

import java.text.NumberFormat
import java.util.Locale

object Formatter {
    private val rupiah: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    /** Format angka menjadi mata uang Rupiah, mis. 145000 -> "Rp145.000". */
    fun rupiah(amount: Long): String = rupiah.format(amount)
}
