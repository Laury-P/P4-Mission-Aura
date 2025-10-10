package com.aura.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LoginResponse (
    @Json(name = "granted")
    val granted: Boolean,
)

// TODO: Add toDomainModel() if the API structure changes or becomes more complex