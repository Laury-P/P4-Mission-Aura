package com.aura.ui

import androidx.annotation.StringRes
import com.aura.R

enum class UIMessage {
    NETWORK,
    CREDENTIAL_DENIED,
    CREDENTIAL_ACCEPTED,
    UNKNOWN;


    @get:StringRes
    val translatedMessage: Int
        get() = when (this) {
            NETWORK -> R.string.error_network
            CREDENTIAL_DENIED-> R.string.login_refused
            CREDENTIAL_ACCEPTED -> R.string.login_success
            UNKNOWN -> R.string.error_unknown
        }
}