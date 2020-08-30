package vn.hexagon.vietnhat.repository.chat

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.remote.ChatDetailResponse
import vn.hexagon.vietnhat.data.model.remote.ChatResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-09-29.
 */
class ChatRepository @Inject constructor(
  private val service: NetworkService
) {
  fun getContentChat(userId: String, userChatId: String): Single<ChatDetailResponse> {
    return service.getContentChat(userId = userId, userChatId = userChatId)
  }

  fun sendMessageChat(
    userId: String,
    userReceiveId: String,
    content: String
  ): Single<ChatResponse> {
    return service.sendMessageChat(
      userIdSend = userId,
      userIdReceive = userReceiveId,
      content = content
    )
  }
}