package com.aura.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
import com.aura.data.model.LoginResponse
import com.aura.data.repository.AuraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginVewModel @Inject constructor(private val repository: AuraRepository): ViewModel(){

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _identifier = MutableStateFlow("")
    val identifier: StateFlow<String> = _identifier.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    val isLoginEnabled = combine(
        _identifier,
        _password,
        _loginState) { identifier, password, state ->
        identifier.isNotEmpty() && password.isNotEmpty() && !state.loading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)



    fun onIdentifierChanged(newIdentifier: String){
        _identifier.value = newIdentifier
    }

    fun onPasswordChanged(password: String){
        _password.value = password
    }


    suspend fun login(identifier : String = _identifier.value, password : String = _password.value){

        repository.login(identifier, password)
            .onEach { result ->
                when(result){
                    is Result.Success -> _loginState.update{
                        val granted = result.data.granted
                        if(granted){
                            SessionManager.startSession(identifier)
                        }
                        it.copy(
                            loginResult = granted,
                            loading = false,
                            errorMessage = null,
                        )
                    }
                    is Result.Error -> _loginState.update{
                        it.copy(
                            errorMessage = it.errorMessage,
                            loading = false,
                            loginResult = null,
                        )
                    }
                    Result.Loading -> _loginState.update{
                        it.copy(
                            loading = true,
                            errorMessage = null,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}

data class LoginState(
    val loginResult: Boolean? = null,
    val loading: Boolean = false,
    val errorMessage: Int? = null
)
