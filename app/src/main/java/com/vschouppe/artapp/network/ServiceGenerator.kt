package com.vschouppe.artapp.network
//
//import com.google.auth.oauth2.AccessToken
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import com.squareup.okhttp.OkHttpClient
//import kotlinx.serialization.json.Json
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//
//
//
//object ServiceGenerator {
//    fun <S> createService(serviceClass: Class<S>?, baseUrl: String?): S {
//        return createService(serviceClass, baseUrl, null)
//    }
//
//    fun <S> createService(serviceClass: Class<S>?, baseUrl: String?, accessToken: AccessToken?): S {
//
//        val retrofit = Retrofit.Builder()
//            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
////    .addConverterFactory(GsonConverterFactory.create())
//            .baseUrl(baseUrl)
//            .build()
//
//
//        retrofit.create(GoogleAccountsService::class.java)
//        if (accessToken != null) {
//            val okHttpClient = OkHttpClient.Builder()
//                .addInterceptor { chain ->
//                    val originalRequest = chain.request()
//                    val newRequest = originalRequest.newBuilder()
//                        .addHeader("Accept", "application/json;versions=1")
////                        .addHeader("Authorization", "${accessToken.tokenType} ${accessToken.accessToken}")
//                        .addHeader("Authorization", "${accessToken.tokenValue}")
//                        .build()
//                    chain.proceed(newRequest)
//                }
//                .build()
//        }
//        val adapter= retrofit.create(GoogleAccountsService::class.java)
//        return adapter
//    }
//}
