package vn.hexagon.vietnhat.data.model.news

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-12-19 
 */
data class NewsDetailResponse(
    @SerializedName("data")
    val data: News,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)