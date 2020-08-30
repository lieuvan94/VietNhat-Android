package vn.hexagon.vietnhat.data.model.service

import com.google.gson.annotations.SerializedName
import vn.hexagon.vietnhat.data.model.post.Post

/**
 * Created by VuNBT on 9/23/2019.
 */
data class ListPostResponse(
    @SerializedName("data")
    val data:ArrayList<Post>,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)