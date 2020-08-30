package vn.hexagon.vietnhat.data.model.remote

import com.google.gson.annotations.SerializedName
import vn.hexagon.vietnhat.base.utils.Utils

data class ChatContent(
  val content: String,
  val time: String,
  @SerializedName("user_id_send") val userIdSend: String?,
  @SerializedName("user_id_receive") var userIdReceive: String
) {
  val timeConverted
    get() = Utils.formatDateTime(time)
}