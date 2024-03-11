package com.vschouppe.artapp.network

import kotlinx.serialization.Serializable

@Serializable
abstract class BaseResponse {

    // Explicitly declare the `getError` method with @JvmName
    @get:JvmName("getErrorProperty")
    val error: String? = null

    @JvmName("getErrorFunction")
    fun getError(): String? {
        return error
    }
}