package com.aura.data.repository

sealed class Result<out T> {
    object Loading : Result<Nothing>()

    data class Success<out R>(val data: R) : Result<R>()

    data class Error(val exception: Exception) : Result<Nothing>()
}