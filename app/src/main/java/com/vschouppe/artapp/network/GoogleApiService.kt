package com.vschouppe.artapp.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.vschouppe.artapp.model.google.Album
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header


private const val BASE_URL =
    "https://photoslibrary.googleapis.com/v1/"

/**
 * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getPhotos] method
 */
interface GoogleApiService {
    /**
     * Returns a [List] of [Album] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "albums" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("albums")
    suspend fun getAlbums(@Header("Authorization") accessToken: String): List<Album>
}


/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object GoogleApi {
    val retrofitService: GoogleApiService by lazy {
        retrofit.create(GoogleApiService::class.java)
    }
}
