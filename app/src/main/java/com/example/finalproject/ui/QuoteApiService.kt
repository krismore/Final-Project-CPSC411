package com.example.finalproject.ui

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// REST GET (ZenQuotes)
data class QuoteResponse(
    val q: String, // quote
    val a: String  // author
)

interface QuoteApi {
    @GET("today")
    suspend fun getQuote(): List<QuoteResponse>
}

// REST POST (ReqRes)
data class MoodPostBody(
    val mood: String,
    val reflection: String
)

data class MoodPostResponse(
    val id: String,
    val createdAt: String
)

interface MoodPostApi {
    @POST("users")
    suspend fun postMood(@Body body: MoodPostBody): MoodPostResponse
}

object QuoteApiService {
    val quoteApi: QuoteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }

    val postApi: MoodPostApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://reqres.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoodPostApi::class.java)
    }
}

