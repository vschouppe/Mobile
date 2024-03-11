/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vschouppe.artapp.network

import com.google.android.gms.fido.u2f.api.messagebased.ResponseType
import com.google.auth.oauth2.AccessToken
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL =
    "https://accounts.google.com/o/oauth2/v2/"

/**
 * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
//    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

/**
 * Retrofit service object for creating api calls
 */
interface GoogleAuthApiService {
    @GET("auth")
    fun getAuthorization(
        @Query("redirect_uri") redirectUri: String?,
        @Query("prompt") prompt: String?,
        @Query("response_type") responseType: String?,
        @Query("client_id") clientId: String,
        @Query("scope") scope: String?,
        @Query("access_type") accessType: String?
    ): Call<AccessToken?>?
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object GoogleAuthApi {
    val retrofitService: GoogleAuthApiService by lazy {
        retrofit.create(GoogleAuthApiService::class.java)
    }
}