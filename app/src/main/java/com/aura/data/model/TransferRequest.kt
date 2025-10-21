package com.aura.data.model

import com.squareup.moshi.Json

data class TransferRequest (
    @Json(name = "sender")
    val senderId: String,
    @Json(name = "recipient")
    val receiverId: String,
    val amount: Double,
)
