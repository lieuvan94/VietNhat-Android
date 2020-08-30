package vn.hexagon.vietnhat.data.model.auth

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-10-31 
 */
class VerifyResponse(
    @SerializedName("data")
    val verifyCd: String,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)