package com.aura.data.repository

import com.aura.data.model.AccountResponse
import com.aura.data.model.LoginRequest
import com.aura.data.model.LoginResponse
import com.aura.data.model.TransferRequest
import com.aura.data.model.TransferResponse
import com.aura.data.network.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuraRepository @Inject constructor(private val apiService: APIService) {

    /**
     * This method call the api to check the id and password provided
     */
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

    /**
     * This method call the api to get the account of the user
     */
    suspend fun getAccount(identifier: String): Flow<Result<List<AccountResponse>>> = flow {
        emit(Result.Loading)
        try {
            val accounts = apiService.getAccount(identifier)
            emit(Result.Success(accounts))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

    }

    /**
     * This method call the api to transfer money from one account to another
     */
    suspend fun transfer(
        senderId: String,
        receiverId: String,
        amount: Double
    ): Flow<Result<TransferResponse>> = flow {
        emit(Result.Loading)

        val transferRequest = TransferRequest(senderId, receiverId, amount)
        try {
            val transferResponse = apiService.transfer(transferRequest)
            emit(Result.Success(transferResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

    }

}