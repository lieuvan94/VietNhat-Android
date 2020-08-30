package vn.hexagon.vietnhat.data.model.product

import com.google.gson.annotations.SerializedName

/*
 * Create by VuNBT on 2019-10-04 
 */
data class ProductResponse(
    @SerializedName("data")
    val data: List<Product>,
    @SerializedName("errorId")
    val errorId: String,
    @SerializedName("message")
    val message: String
)