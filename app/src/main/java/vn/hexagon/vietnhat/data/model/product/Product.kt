package vn.hexagon.vietnhat.data.model.product

import com.google.gson.annotations.SerializedName

/**
 * Created by VuNBT on 9/22/2019.
 */
data class Product(
    @SerializedName("name")
    var name:String,
    @SerializedName("price")
    var price:String,
    @SerializedName("img")
    var img:String
)