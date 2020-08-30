package vn.hexagon.vietnhat.data.model.auth

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-09-11 
 */
class LoginResponse(
    @SerializedName("data")
    val user: User,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String)