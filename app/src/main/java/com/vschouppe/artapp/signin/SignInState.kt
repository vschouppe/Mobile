package com.vschouppe.artapp.signin

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)