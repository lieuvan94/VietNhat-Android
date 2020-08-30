package vn.hexagon.vietnhat.data.model.translator

import com.google.gson.annotations.SerializedName

/**
 * Created by VuNBT on 9/16/2019.
 */
class TranslatorListResponse(
    @SerializedName("data")
    val data:List<Translator>,
    @SerializedName("errorId")
    val errorId: String,
    @SerializedName("message")
    val message: String
)