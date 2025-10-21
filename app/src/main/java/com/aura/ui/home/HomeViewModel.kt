package com.aura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.model.AccountResponse
import com.aura.data.repository.AuraRepository
import com.aura.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.aura.data.repository.Result
import com.aura.ui.UIMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: AuraRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiMessageFlow = MutableSharedFlow<UIMessage>()
    val uiMessageFlow = _uiMessageFlow.asSharedFlow()


    private val identifier = SessionManager.getCurrentUserId()

    suspend fun getAccount() {
        if (identifier != null) {
            repository.getAccount(identifier)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> _uiState.update { it ->
                            val accounts = result.data
                            val mainAccount = accounts.find { it.mainAccount }
                            it.copy(
                                balanceMain = mainAccount?.balance ?: 0.0,
                                accounts = accounts,
                                loading = false,
                                isRetryVisible = false,
                            )
                        }

                        is Result.Error -> _uiState.update {
                            when (result.exception) {
                                is IOException -> viewModelScope.launch {
                                    _uiMessageFlow.emit(
                                        UIMessage.NETWORK
                                    )
                                }

                                else -> viewModelScope.launch { _uiMessageFlow.emit(UIMessage.UNKNOWN) }
                            }
                            it.copy(
                                isRetryVisible = true,
                                loading = false,
                                accounts = emptyList(),
                                balanceMain = 0.0
                            )
                        }

                        Result.Loading -> _uiState.update {
                            it.copy(
                                loading = true,
                                isRetryVisible = false,
                                accounts = emptyList(),
                                balanceMain = 0.0
                            )
                        }
                    }

                }
                .launchIn(viewModelScope)
        }

    }
}

data class HomeUiState(
    val balanceMain: Double = 0.0,
    val accounts: List<AccountResponse> = emptyList(),
    val loading: Boolean = false,
    val isRetryVisible: Boolean = false
)