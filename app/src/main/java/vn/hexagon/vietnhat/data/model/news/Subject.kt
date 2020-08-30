package vn.hexagon.vietnhat.data.model.news

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-10-18 
 */
data class Subject(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("date")
    val date: String
)