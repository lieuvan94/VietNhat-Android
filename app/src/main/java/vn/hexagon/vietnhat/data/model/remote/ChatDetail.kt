package vn.hexagon.vietnhat.data.model.remote

import com.google.gson.annotations.SerializedName

data class ChatDetail(
  val active: String,
  val content: ArrayList<ChatContent>,
  val date: String,
  val id: String,
  @SerializedName("number_unseen_1") val numberUnseen1: String,
  @SerializedName("number_unseen_2") val numberUnseen2: String,
  @SerializedName("time_updated") val timeUpdated: String,
  @SerializedName("user_avatar_1") val userAvatar1: String,
  @SerializedName("user_avatar_2") val userAvatar2: String,
  @SerializedName("user_id_receive") val userIdReceive: String,
  @SerializedName("user_id_send") val userIdSend: String,
  @SerializedName("user_name_1") val userName1: String,
  @SerializedName("user_name_2") val userName2: String
)