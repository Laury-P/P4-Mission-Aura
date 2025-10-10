package com.aura.data.model

import com.squareup.moshi.Json

data class TransferRequest (
    @Json(name = "sender")
    val senderId: Int,
    @Json(name = "receiver")
    val receiverId: Int,
    val amount: Double,
)
