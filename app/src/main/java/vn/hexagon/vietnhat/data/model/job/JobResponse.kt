package vn.hexagon.vietnhat.data.model.job

import com.google.gson.annotations.SerializedName

/**
 * Created by VuNBT on 9/22/2019.
 */
class JobResponse(
    @SerializedName("data")
    val job:Array<Job>,
    @SerializedName("errorId")
    val errorId:String,
    @SerializedName("message")
    val message:String
)