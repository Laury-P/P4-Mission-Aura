package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
import com.aura.data.repository.AuraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.IOException

@HiltViewModel
class LoginVewModel @Inject constructor(private val repository: AuraRepository) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _errorMessage = MutableSharedFlow<ErrorMessage>()
    val errorMessage = _errorMessage.asSharedFlow()


    var identifier: String = ""

    private var password: String = ""


    fun onIdentifierChanged(newIdentifier: String) {
        identifier = newIdentifier
        updateLogin()
    }

    fun onPasswordChanged(newPass: String) {
        password = newPass
        updateLogin()
    }

    fun updateLogin(){
        _loginState.update {
            it.copy(isLoginEnabled = (identifier.isNotBlank() && password.isNotBlank() && !it.loading) )
        }
    }


    suspend fun login(id: String = identifier, pass: String = password) {

            repository.login(id, pass)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> _loginState.update {
                            val granted = result.data.granted
                            if (granted) {
                                SessionManager.startSession(id)
                            }
                            it.copy(
                                loginResult = granted,
                                loading = false,
                                errorMessage = if (granted) null else ErrorType.CREDENTIAL,
                                isLoginEnabled = true,
                            )
                        }

                        is Result.Error -> _loginState.update {
                            val message = when (result.exception) {
                                is IOException -> ErrorType.NETWORK
                                else -> ErrorType.UNKNOWN
                            }
                            it.copy(
                                errorMessage = message,
                                loading = false,
                                loginResult = null,
                                isLoginEnabled = true,
                            )
                        }

                        Result.Loading -> _loginState.update {
                            it.copy(
                                loading = true,
                                errorMessage = null,
                                loginResult = null,
                                isLoginEnabled = false
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
    }

}
data class ErrorMessage (
    val isError: Boolean = false,
    val message: ErrorType? = null
)
data class LoginState(
    val loginResult: Boolean? = null,
    val loading: Boolean = false,
    val errorMessage: ErrorType? = null,
    val isLoginEnabled: Boolean = false,
)
