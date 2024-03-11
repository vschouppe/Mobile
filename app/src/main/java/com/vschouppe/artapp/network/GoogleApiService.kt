package com.vschouppe.artapp.network

import android.provider.ContactsContract
import retrofit2.http.GET

interface GoogleApisService {
    @get:GET("/oauth2/v1/userinfo?alt=json")
    val profile: ContactsContract.Profile?

    companion object {
        const val BASE_URL = "https://www.googleapis.com"
    }
}


//package com.vschouppe.artapp.network
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import kotlinx.serialization.json.Json
//import okhttp3.MediaType.Companion.toMediaType
//import retrofit2.Retrofit
//import retrofit2.http.GET
//import retrofit2.http.Header
//
//private const val BASE_URL =
//    "https://photoslibrary.googleapis.com/"
//
//
///**
// * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
// */
//private val retrofit = Retrofit.Builder()
//    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
//    .baseUrl(BASE_URL)
//    .build()
//
//interface GoogleApiService {
//    @GET("v1/albums")
//    suspend fun getAlbums(@Header("Authorization") authorization : String) : String
//    abstract fun getAlbums(): String
//}
//
//object GoogleApi {
//    val retrofitService : GoogleApiService by lazy {
//        retrofit.create(GoogleApiService::class.java)
//    }
//}
