package com.aura.data.model

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "id")
    val identifier: String,
    val password: String
)

