package com.vschouppe.artapp.signin

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String?,
    val username: String?,
    val profilePictureUrl: String?
)

data class UserAddress(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var countryName: String = "",
    var city: String = ""
)
