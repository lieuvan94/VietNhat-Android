package vn.hexagon.vietnhat.data.model.translator

import com.google.gson.annotations.SerializedName

/**
 * Created by VuNBT on 9/14/2019.
 */
data class PostResponse (
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)