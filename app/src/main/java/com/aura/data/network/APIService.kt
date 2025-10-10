package com.aura.data.network

import com.aura.data.model.AccountResponse
import com.aura.data.model.LoginRequest
import com.aura.data.model.LoginResponse
import com.aura.data.model.TransferRequest
import com.aura.data.model.TransferResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {

    @POST("/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse


    @GET("/accounts/{id}")
    suspend fun getAccount(
        @Query(value = "id") identifier: String,
    ): AccountResponse

    @POST("/transfer")
    suspend fun transfer(
        @Body transferRequest: TransferRequest
    ): TransferResponse

}