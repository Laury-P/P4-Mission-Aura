package com.aura.ui

import androidx.annotation.StringRes
import com.aura.R

enum class UIMessage {
    NETWORK,
    CREDENTIAL_DENIED,
    CREDENTIAL_ACCEPTED,
    UNKNOWN,
    TRANSFER_ACCEPTED,
    TRANSFER_DENIED,
    TRANSFER_ERROR;


    @get:StringRes
    val translatedMessage: Int
        get() = when (this) {
            NETWORK -> R.string.error_network
            CREDENTIAL_DENIED -> R.string.login_refused
            CREDENTIAL_ACCEPTED -> R.string.login_success
            UNKNOWN -> R.string.error_unknown
            TRANSFER_ACCEPTED -> R.string.transfer_success
            TRANSFER_DENIED -> R.string.transfer_refused
            TRANSFER_ERROR -> R.string.transfer_error
        }
}

