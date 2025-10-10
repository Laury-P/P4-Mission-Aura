package com.aura.data.model



data class User (
    val identifier: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val account: List<Account>,
    )