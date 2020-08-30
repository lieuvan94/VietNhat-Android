package vn.hexagon.vietnhat.data.model.auth

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-11-01 
 */
class PasswordResponse(
    @SerializedName("data")
    val data: String,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)