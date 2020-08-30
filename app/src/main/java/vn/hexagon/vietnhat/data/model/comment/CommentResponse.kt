package vn.hexagon.vietnhat.data.model.comment

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-12-23 
 */
data class CommentResponse(
    @SerializedName("data")
    val data:Comment,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)