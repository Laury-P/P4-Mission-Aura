package com.aura.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountResponse(
    @Json(name = "id")
    val accountId: Int,
    @Json(name = "main")
    val mainAccount: Boolean,
    @Json(name = "balance")
    val balance: Double,
)


// TODO: Add toDomainModel() if the API structure changes or becomes more complex
