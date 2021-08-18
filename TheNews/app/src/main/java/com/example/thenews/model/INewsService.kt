package com.example.thenews.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface INewsService {

    @GET("top-headlines/?apikey=09318fd3d4364e0a896f1cc35e5c964b&country=us")
    fun getNewsTopHeadlines(@Query("category") category: String, @Query("pageSize") pageSize : Int): Call<NewsModel>

    @GET("everything/?apikey=09318fd3d4364e0a896f1cc35e5c964b")
    fun getNewsEverything(@Query("q") query: String, @Query("pageSize") pageSize : Int): Call<NewsModel>

    companion object{
         fun getRetrofit(): Retrofit {
             return Retrofit.Builder()
                 .baseUrl("https://newsapi.org/v2/")
                 .addConverterFactory(GsonConverterFactory.create())
                 .build()
         }

        fun getClient(): INewsService{
           return getRetrofit().create(INewsService::class.java)
        }
    }
}
