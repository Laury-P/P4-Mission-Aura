package com.aura.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginVewModel @Inject constructor(): ViewModel(){

    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled.asStateFlow()
    private val _identifier = MutableStateFlow("")
    val identifier: StateFlow<String> = _identifier.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()


    fun onIdentifierChanged(newIdentifier: String){
        _identifier.value = newIdentifier
        validateInputs()
    }

    fun onPasswordChanged(password: String){
        _password.value = password
        validateInputs()
    }
    private fun validateInputs(){
        _isLoginEnabled.value = _identifier.value.isNotEmpty() && _password.value.isNotEmpty()
    }
}
