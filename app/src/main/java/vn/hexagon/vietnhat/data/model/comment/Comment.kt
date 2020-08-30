package vn.hexagon.vietnhat.data.model.comment

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-12-23 
 */
data class Comment(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val userNm: String,
    @SerializedName("post_id")
    val postId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("user_avatar")
    val userAvatar: String,
    @SerializedName("user_phone")
    val userPhone: String
)