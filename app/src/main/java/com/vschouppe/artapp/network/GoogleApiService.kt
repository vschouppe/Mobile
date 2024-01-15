package com.vschouppe.artapp.network
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

private const val BASE_URL =
    "https://photoslibrary.googleapis.com/"


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface GoogleApiService {
    @GET("v1/albums")
    suspend fun getAlbums(@Header("Authorization") authorization : String) : String
}

object GoogleApi {
    val retrofitService : GoogleApiService by lazy {
        retrofit.create(GoogleApiService::class.java)
    }
}