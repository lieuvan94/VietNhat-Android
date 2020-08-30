package vn.hexagon.vietnhat.data.model.news

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-10-18 
 */
class SubjectResponse(
    @SerializedName("data")
    val subject: Array<Subject>,
    @SerializedName("errorId")
    val errorId: String,
    @SerializedName("message")
    val message: String
)