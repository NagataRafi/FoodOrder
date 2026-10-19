package com.example.foodorder.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val localeId = Locale("id", "ID")
    private val numberFormat = NumberFormat.getNumberInstance(localeId)

    /**
     * Formats integer amount to Indonesian Rupiah standard display string.
     * Example: 25000 -> "Rp25.000"
     */
    fun formatRupiah(amount: Int): String {
        return "Rp${numberFormat.format(amount)}"
    }
}
