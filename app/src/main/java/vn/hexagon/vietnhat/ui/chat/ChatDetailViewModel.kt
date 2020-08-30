package vn.hexagon.vietnhat.ui.chat

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.constant.APIConstant
import vn.hexagon.vietnhat.data.model.remote.ChatContent
import vn.hexagon.vietnhat.data.model.remote.ChatDetail
import vn.hexagon.vietnhat.data.model.remote.SocketResponse
import vn.hexagon.vietnhat.data.remote.SocketService
import vn.hexagon.vietnhat.repository.chat.ChatRepository
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-09-22.
 */
class ChatDetailViewModel @Inject constructor(
  private val repo: ChatRepository,
  private val socketIO: SocketService
) : MVVMBaseViewModel() {
  var detailChatLiveData = MutableLiveData<Pair<Boolean, ChatDetail>>()
  private var chatDetail: ChatDetail? = null

  fun getHistoryChat(
    userId: String,
    userChatId: String
  ) {
    repo.getContentChat(userId, userChatId)
      .applyScheduler()
      .subscribe({ response ->
        chatDetail = response.data
        detailChatLiveData.postValue(Pair(false, response.data) as Pair<Boolean, ChatDetail>?)
      }, { error ->
        DebugLog.e(error.toString())
      })
      .addToCompositeDisposable(compositeDisposable)
  }

  fun sendMessage(
    userId: String,
    userReceiveId: String,
    content: String
  ) {
    repo.sendMessageChat(userId, userReceiveId, content)
      .applyScheduler()
      .subscribe({ response ->
        response.data.userIdReceive = userReceiveId
        chatDetail?.content?.add(response.data)
        detailChatLiveData.postValue(Pair(true, chatDetail) as Pair<Boolean, ChatDetail>?)
        val socketData = SocketResponse(APIConstant.SOCKET_EVENT_RECEIVE_MESSAGE, response.data)
        socketIO.request(APIConstant.SOCKET_EVENT_RECEIVE_MESSAGE, Utils.toJson(socketData))
          .applyScheduler()
          .subscribe({},
            { socketError ->
              DebugLog.e(socketError.toString())
            })
          .addToCompositeDisposable(compositeDisposable)
      }, { error ->
        DebugLog.e(error.toString())
      })
      .addToCompositeDisposable(compositeDisposable)
  }

  fun subIncomingMessage(
    userId: String,
    userReceiveId: String
  ) {
    socketIO.handleResponse(APIConstant.SOCKET_EVENT_SEND_MESSAGE)
      .toMainThread()
      .subscribe { response ->
        val socketResponse = Utils.fromJson<SocketResponse>(response)
        when (socketResponse.event) {
          APIConstant.SOCKET_EVENT_RECEIVE_MESSAGE -> {
            val data: ChatContent =
              Utils.fromJson(JSONObject(socketResponse.data as MutableMap<*, *>).toString())
            if (userId == data.userIdReceive && userReceiveId == data.userIdSend) {
              chatDetail?.content?.add(data)
              detailChatLiveData.postValue(Pair(false, chatDetail) as Pair<Boolean, ChatDetail>?)
            }
          }
        }
      }
      .addToCompositeDisposable(compositeDisposable)
  }
}