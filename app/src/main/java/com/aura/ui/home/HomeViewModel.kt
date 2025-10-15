package com.aura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import java.io.IOException

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: AuraRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val identifier = SessionManager.getCurrentUserId()

    suspend fun getAccount() {
        if (identifier != null) {
            repository.getAccount(identifier)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> _uiState.update {
                            val accounts = result.data
                            val mainAccount = accounts.find { it.mainAccount == true }
                            it.copy(
                                balanceMain = mainAccount?.balance ?: 0.0,
                                accounts = accounts,
                                errorMessage = null,
                                loading = false
                            )
                        }

                        is Result.Error -> _uiState.update {
                            val message = when(result.exception){
                                is IOException -> R.string.error_network
                                else -> R.string.error_unknown
                            }
                            it.copy(
                                errorMessage = message,
                                loading = false,
                                accounts = emptyList(),
                                balanceMain = 0.0
                            )
                        }

                        Result.Loading -> _uiState.update {
                            it.copy(
                                loading = true,
                                errorMessage = null,
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
    val errorMessage: Int? = null,
    val loading: Boolean = false,
)