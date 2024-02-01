package com.vschouppe.artapp.signin

import androidx.compose.ui.res.stringResource
import com.vschouppe.artapp.R

data class SignInState(
    val welcomeText: String = R.string.signin_welcome_initial.toString(),
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val address: UserAddress? = null
    )