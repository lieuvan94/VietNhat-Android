package vn.hexagon.vietnhat.data.model.news

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-12-19 
 */
class NewsListResponse(
    @SerializedName("data")
    val data:ArrayList<News>,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)