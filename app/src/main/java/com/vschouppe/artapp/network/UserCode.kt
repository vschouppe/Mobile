package com.vschouppe.artapp.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
class UserCode : BaseResponse() {
    @SerializedName("device_code")
    val deviceCode: String? = null

    @SerializedName("user_code")
    val userCode: String? = null

    @SerializedName("verification_url")
    val verificationUrl: String? = null

    @SerializedName("expires_in")
    val expiresIn: Long? = null
    val interval: Int? = null

    override fun toString(): String {
        return if (super.getError() != null) {
            "UserCode{error='" + super.getError() + "'}"
        } else "UserCode{" +
                "deviceCode='" + deviceCode + '\'' +
                ", userCode='" + userCode + '\'' +
                ", verificationUrl='" + verificationUrl + '\'' +
                ", expiresIn=" + expiresIn +
                ", interval=" + interval +
                '}'
    }
}