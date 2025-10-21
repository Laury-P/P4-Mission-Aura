package com.aura.data.repository

import com.aura.data.model.AccountResponse
import com.aura.data.model.LoginRequest
import com.aura.data.model.LoginResponse
import com.aura.data.model.TransferRequest
import com.aura.data.model.TransferResponse
import com.aura.data.network.APIService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuraRepository @Inject constructor (private val apiService: APIService) {

    suspend fun login(identifier: String, password: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        //delay(2000)

        val loginRequest = LoginRequest(identifier, password)

        try {
            val loginResponse = apiService.login(loginRequest)
            emit(Result.Success(loginResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    //var tryNumber = 0 // to simulate network error

    suspend fun getAccount(identifier: String): Flow<Result<List<AccountResponse>>> = flow {
        emit(Result.Loading)
        try {
            /**
             * To simulate network error
            delay(2000)
            if ( tryNumber < 2 ) {
                delay(2000)
                emit(Result.Error(Exception("Erreur de connexion au serveur")))
                tryNumber += 1
            } else {
                Place val account and emit here
            }
             */
            val accounts = apiService.getAccount(identifier)
            emit(Result.Success(accounts))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

    }

    suspend fun transfer(senderId: String, receiverId: String, amount: Double): Flow<Result<TransferResponse>> = flow {
        emit(Result.Loading)

        val transferRequest = TransferRequest(senderId, receiverId, amount)
        try{
            val transferResponse = apiService.transfer(transferRequest)
            emit(Result.Success(transferResponse))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

    }

}