package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.AuraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import com.aura.ui.UIMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel
class LoginVewModel @Inject constructor(private val repository: AuraRepository) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _uiMessageFlow = MutableSharedFlow<UIMessage>()
    val uiMessageFlow = _uiMessageFlow.asSharedFlow()


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

    private fun updateLogin() {
        _loginState.update {
            it.copy(isLoginEnabled = (identifier.isNotBlank() && password.isNotBlank() && !it.isLoading))
        }
    }


    suspend fun login(id: String = identifier, pass: String = password) {

        repository.login(id, pass).onEach { result ->
                when (result) {
                    is Result.Success -> _loginState.update {
                        val granted = result.data.granted
                        if (granted) {
                            SessionManager.startSession(id)
                            viewModelScope.launch { _uiMessageFlow.emit(UIMessage.CREDENTIAL_ACCEPTED) }
                        } else {
                            viewModelScope.launch { _uiMessageFlow.emit(UIMessage.CREDENTIAL_DENIED) }
                        }
                        it.copy(
                            isLoginGranted = granted,
                            isLoading = false,
                            isLoginEnabled = true,
                        )
                    }

                    is Result.Error -> _loginState.update {
                        when (result.exception) {
                            is IOException -> viewModelScope.launch { _uiMessageFlow.emit(UIMessage.NETWORK) }
                            else -> viewModelScope.launch { _uiMessageFlow.emit(UIMessage.UNKNOWN) }
                        }
                        it.copy(
                            isLoading = false,
                            isLoginGranted = null,
                            isLoginEnabled = true,
                        )
                    }

                    Result.Loading -> _loginState.update {
                        it.copy(
                            isLoading = true, isLoginGranted = null, isLoginEnabled = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

}

data class LoginState(
    val isLoginGranted: Boolean? = null,
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false,
)
