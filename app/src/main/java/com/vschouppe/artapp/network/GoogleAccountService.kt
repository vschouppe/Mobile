package com.vschouppe.artapp.network

import android.util.Log
import com.google.auth.oauth2.AccessToken
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.http.*


/**
 * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(GoogleAccountsService.BASE_URL)
    .client(OkHttpClient.Builder().addInterceptor(RequestLoggingInterceptor()).build()) // Add logging interceptor
    .build()

interface GoogleAccountsService {
//    @POST("/o/oauth2/v2/auth")
    @POST("/o/oauth2/device/code")
//    @POST("oauth2/v4/token")
    @FormUrlEncoded
    suspend fun getUserCode(
        @Field("client_id")
        clientId: String,
        @Field("scope")
        scope: String?
    ): UserCode?
//    ): List<UserCode>?

    @POST("/o/oauth2/token")
    @FormUrlEncoded
    fun getAccessToken(
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("code") code: String?,
        @Field("grant_type") grantType: String?
    ): AccessToken?

    @POST("/o/oauth2/token")
    @FormUrlEncoded
    fun refreshAccessToken(
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("refresh_token") refreshToken: String?,
        @Field("grant_type") grantType: String?
    ): AccessToken?

    companion object{
        var BASE_URL = "https://accounts.google.com"
//        var BASE_URL = "https://www.googleapis.com"
        var ACCESS_GRANT_TYPE = "http://oauth.net/grant_type/device/1.0"
        var REFRESH_GRANT_TYPE = "refresh_token"
    }
}


/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object GoogleAccountApi {
    val retrofitService: GoogleAccountsService by lazy {
        retrofit.create(GoogleAccountsService::class.java)
    }
}


/**
 * Custom Interceptor to log request details
 */
class RequestLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Log the request URL and body
        Log.d("RequestLogging", "Request URL: ${request.url}")
        Log.d("RequestLogging", "Request Body: ${request.body?.asString()}")

        // Proceed with the request and log the response
        val response = chain.proceed(request)

        // Log the response details
        Log.d("RequestLogging", "Response Code: ${response.code}")
        Log.d("RequestLogging", "Response Body: ${response.body?.string()}")

        return response
    }

    private fun RequestBody?.asString(): String {
        val buffer = Buffer()
        this?.writeTo(buffer)
        return buffer.readUtf8()
    }
}