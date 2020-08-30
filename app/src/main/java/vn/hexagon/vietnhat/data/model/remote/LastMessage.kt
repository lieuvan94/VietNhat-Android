package vn.hexagon.vietnhat.data.model.remote

import com.google.gson.annotations.SerializedName
import vn.hexagon.vietnhat.base.utils.Utils
import java.io.Serializable

data class LastMessage(
  val content: String,
  val time: String,
  @SerializedName("user_id_send") val userIdSend: String
) : Serializable {
  val timeConverted
    get() = Utils.formatDateTime(time)
}