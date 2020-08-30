package vn.hexagon.vietnhat.data.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Created by VuNBT on 2019-08-10.
 */
data class User(
    @SerializedName("id")
    val userId:String,
    @SerializedName("fbid")
    val faceId:String,
    @SerializedName("ggid")
    val googleId:String,
    @SerializedName("name")
    val name:String,
    @SerializedName("phone")
    val phone:String,
    @SerializedName("password")
    val password:String,
    @SerializedName("email")
    val email:String,
    @SerializedName("avatar")
    val avatar:String,
    @SerializedName("account")
    val account:String,
    @SerializedName("date")
    val date:String,
    @SerializedName("active")
    val active:String,
    @SerializedName("address")
    val address:String)