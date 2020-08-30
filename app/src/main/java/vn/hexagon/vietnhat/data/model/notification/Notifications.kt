package vn.hexagon.vietnhat.data.model.notification

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-09-30 
 */
data class Notifications(
    @SerializedName("id")
    val id:String,
    @SerializedName("title")
    val title:String,
    @SerializedName("content")
    val content:String,
    @SerializedName("date")
    val date:String,
    @SerializedName("is_read")
    val isRead:Int
)