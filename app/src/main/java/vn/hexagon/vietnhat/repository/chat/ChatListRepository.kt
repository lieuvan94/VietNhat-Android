package vn.hexagon.vietnhat.repository.chat

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.remote.ChatListResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-09-29.
 */
class ChatListRepository @Inject constructor(
  private val service: NetworkService
) {
  fun getListChat(userId: String): Single<ChatListResponse> {
    return service.getListChat(userId)
  }
}