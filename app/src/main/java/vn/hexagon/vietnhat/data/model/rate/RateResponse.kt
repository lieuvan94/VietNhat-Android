package vn.hexagon.vietnhat.data.model.rate

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-09-30 
 */
data class RateResponse(
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)