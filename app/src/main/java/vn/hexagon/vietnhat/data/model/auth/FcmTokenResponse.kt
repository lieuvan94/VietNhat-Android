package vn.hexagon.vietnhat.data.model.auth

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-10-30 
 */
data class FcmTokenResponse(
    @SerializedName("errorId")
    val errorId: String,
    @SerializedName("message")
    val message: String
)