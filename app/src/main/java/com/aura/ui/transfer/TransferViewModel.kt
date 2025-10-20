package com.aura.ui.transfer

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.aura.data.repository.AuraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(private val repository: AuraRepository) : ViewModel() {
    val _transferState = MutableStateFlow(TransferState())
    val transferState: StateFlow<TransferState> = _transferState.asStateFlow()

    var recipient : String = ""

    var amount : String = ""

    fun onRecipientChanged(newRecipient : String) {
        recipient = newRecipient
        updateTransfer()
    }

    fun onAmountChanged(newAmount : String) {
        amount = newAmount
        updateTransfer()
    }

    fun updateTransfer(){
        _transferState.update {
            it.copy(isTransferEnabled = (recipient.isNotBlank() && amount.isNotBlank() && !it.loading) )
        }
    }
}

data class TransferState(
    val isTransferEnabled : Boolean = false,
    val loading : Boolean = false,
)
