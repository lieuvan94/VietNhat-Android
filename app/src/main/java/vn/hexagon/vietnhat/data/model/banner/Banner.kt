package vn.hexagon.vietnhat.data.model.banner

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-09-30 
 */
data class Banner(
    @SerializedName("id")
    val id:String,
    @SerializedName("name")
    val name:String,
    @SerializedName("img")
    val img:String,
    @SerializedName("date")
    val date:String,
    @SerializedName("active")
    val active:String
)