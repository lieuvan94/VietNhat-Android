package vn.hexagon.vietnhat.data.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatList(
  @SerializedName("last_message") val lastMessage: LastMessage,
  @SerializedName("number_unseen") var numberUnseen: String,
  @SerializedName("user_avatar") val userAvatar: String,
  @SerializedName("user_name") val userName: String,
  @SerializedName("user_id") val userId: String,
  @SerializedName("user_phone") val userPhone: String
) : Serializable