package com.aura.ui.transfer

import java.math.BigDecimal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.AuraRepository
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import com.aura.ui.UIMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(private val repository: AuraRepository) : ViewModel() {
    private val _transferState = MutableStateFlow(TransferState())
    val transferState: StateFlow<TransferState> = _transferState.asStateFlow()

    private val _uiMessageFlow = MutableSharedFlow<UIMessage>()
    val uiMessageFlow = _uiMessageFlow.asSharedFlow()

    private var recipient: String = ""

    private var amount: BigDecimal = BigDecimal("0.0")

    fun onRecipientChanged(newRecipient: String) {
        recipient = newRecipient
        updateTransfer()
    }

    fun onAmountChanged(newAmount: BigDecimal) {
        amount = newAmount
        updateTransfer()
    }

    private fun updateTransfer() {
        _transferState.update {
            it.copy(isTransferEnabled = (recipient.isNotBlank() && amount != BigDecimal("0.0") && !it.isLoading))
        }
    }

    suspend fun transfer(
        senderId: String? = SessionManager.getCurrentUserId(),
        receiverId: String = recipient,
        transferAmount: BigDecimal = amount
    ) {
        if (senderId != null) {
            val amount = transferAmount.toDouble()
            repository.transfer(senderId, receiverId, amount)
                .onEach { result ->
                    when (result) {
                        is Result.Error -> _transferState.update {
                            when (result.exception) {
                                is IOException -> viewModelScope.launch {
                                    _uiMessageFlow.emit(
                                        UIMessage.NETWORK
                                    )
                                }

                                else -> viewModelScope.launch {
                                    _uiMessageFlow.emit(
                                        UIMessage.TRANSFER_ERROR
                                    )
                                }
                            }
                            it.copy(
                                isLoading = false,
                                isTransferGranted = null,
                                isTransferEnabled = true,
                            )
                        }

                        Result.Loading -> _transferState.update {
                            it.copy(
                                isLoading = true,
                                isTransferEnabled = false,
                                isTransferGranted = null,
                            )
                        }

                        is Result.Success -> _transferState.update {
                            val granted = result.data.granted
                            if (granted) {
                                viewModelScope.launch { _uiMessageFlow.emit(UIMessage.TRANSFER_ACCEPTED) }
                            } else {
                                viewModelScope.launch { _uiMessageFlow.emit(UIMessage.TRANSFER_DENIED) }
                            }
                            it.copy(
                                isLoading = false,
                                isTransferGranted = granted,
                                isTransferEnabled = true,
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }
}

data class TransferState(
    val isTransferEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isTransferGranted: Boolean? = null
)
