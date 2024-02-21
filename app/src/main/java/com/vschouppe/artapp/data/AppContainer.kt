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
package com.vschouppe.artapp.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.vschouppe.artapp.network.GoogleApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val googlePhotoRepository: GooglePhotosRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
//    private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/"
//    private val baseUrl = "https://photoslibrary.googleapis.com/v1/albums/"
    private val googleBaseUrl = "https://photoslibrary.googleapis.com/v1/"
//    private val baseUrl = "https://photoslibrary.googleapis.com/v1/mediaItems"
//    private val baseUrl = "https://photoslibrary.googleapis.com/v1/albums"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val googleRetroFit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(googleBaseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val googleRetrofitService: GoogleApiService by lazy {
        googleRetroFit.create(GoogleApiService::class.java)
    }

    /**
     * DI implementation for google photos repository
     */
    override val googlePhotoRepository: GooglePhotosRepository by lazy {
        NetworkGooglePhotosRepository(googleRetrofitService)
    }

}
