package com.aura.data.repository

import com.aura.data.model.AccountResponse
import com.aura.data.model.LoginRequest
import com.aura.data.model.LoginResponse
import com.aura.data.model.TransferRequest
import com.aura.data.model.TransferResponse
import com.aura.data.network.APIService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuraRepository @Inject constructor (private val apiService: APIService) {

    suspend fun login(identifier: String, password: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)

        val loginRequest = LoginRequest(identifier, password)

        try {
            val loginResponse = apiService.login(loginRequest)
            emit(Result.Success(loginResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun getAccount(identifier: String): AccountResponse {
        return apiService.getAccount(identifier)
        //TODO: Add flow et Result
    }

    suspend fun transfer(senderId: Int, receiverId: Int, amount: Double): TransferResponse {
        val transferRequest = TransferRequest(senderId, receiverId, amount)
        return apiService.transfer(transferRequest)
        //TODO: Add flow et Result

    }

}