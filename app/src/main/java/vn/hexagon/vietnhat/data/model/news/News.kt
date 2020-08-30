package vn.hexagon.vietnhat.data.model.news

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-12-19 
 */
data class News (
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("img")
    val img: String
)