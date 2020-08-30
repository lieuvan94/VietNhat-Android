package vn.hexagon.vietnhat.data.model.translator

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Multipart

/**
 * Created by VuNBT on 9/14/2019.
 */
data class Translator(
    @SerializedName("id")
    val id:String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("title")
    val title: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("price")
    val price: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("note")
    val note :String?,
    @SerializedName("is_top")
    val isTop: String?,
    @SerializedName("img")
    val img:String,
    @SerializedName("date")
    val date:String
)