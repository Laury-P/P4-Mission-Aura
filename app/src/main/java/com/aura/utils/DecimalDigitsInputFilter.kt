package com.aura.utils

import android.text.InputFilter
import android.text.Spanned


class DecimalDigitsInputFilter(
    private val digitsAfterZero: Int
) : InputFilter {

    private val regex = Regex("^[0-9]*((\\.[0-9]{0,$digitsAfterZero})?)?$")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val newValue = dest.toString().substring(0, dstart) +
                source.toString().substring(start, end) +
                dest.toString().substring(dend)

        // Si la nouvelle valeur respecte la regex → on autorise
        // sinon on rejette (retourne "")
        return if (regex.matches(newValue)) null else ""
    }
}
