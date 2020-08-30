package vn.hexagon.vietnhat.data.model.job

import com.google.gson.annotations.SerializedName

/**
 * Created by VuNBT on 9/22/2019.
 */
data class Job(
    @SerializedName("id")
    val jobId:String,
    @SerializedName("name")
    val jobName:String
)