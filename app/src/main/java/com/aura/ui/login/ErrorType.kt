package com.aura.ui.login

import androidx.annotation.StringRes
import com.aura.R

enum class ErrorType {
    NETWORK,
    CREDENTIAL,
    UNKNOWN;


    @get:StringRes
    val translatedError: Int
        get() = when (this) {
            NETWORK -> R.string.error_network
            CREDENTIAL -> R.string.login_refused
            UNKNOWN -> R.string.error_unknown
        }
}