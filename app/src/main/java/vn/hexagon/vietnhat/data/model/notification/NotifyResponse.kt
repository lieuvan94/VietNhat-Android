package vn.hexagon.vietnhat.data.model.notification

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-09-30 
 */
data class NotifyResponse(
    @SerializedName("data")
    val data:List<Notifications>,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)