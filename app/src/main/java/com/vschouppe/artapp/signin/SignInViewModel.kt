package com.vschouppe.artapp.signin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun updateAddress(userAddress: UserAddress){
        _state.update { it.copy(
            address = userAddress
        ) }
    }

    fun updateWelcomeText(text: String) {
        _state.update {
            it.copy(
                welcomeText = text
            )
        }
    }
}