package vn.hexagon.vietnhat.data.model.banner

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-09-30 
 */
data class BannerResponse(
    @SerializedName("data")
    val data:ArrayList<Banner>,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)