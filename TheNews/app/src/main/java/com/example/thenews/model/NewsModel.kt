package com.example.thenews.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList


data class Item (
    @SerializedName("title")
    var title: String?,
    @SerializedName("author")
    var author: String? ,
    @SerializedName("description")
    var description: String? ,
    @SerializedName("publishedAt")
    var date: String? ,
    @SerializedName("url")
    val url: String? ,
    @SerializedName("urlToImage")
    var image: String?
)


data class NewsModel(
@SerializedName("status")
val status: String? ,
@SerializedName("totalResults")
val totalResults: String?,
@SerializedName("articles")
val articles: ArrayList<Item>?
)





