package vn.hexagon.vietnhat.data.model.remote

import org.json.JSONArray
import vn.hexagon.vietnhat.base.utils.Utils

/**
 * Created by NhamVD on 2019-09-22.
 */
class ChatListResponse: BaseResponse() {
  val data: List<ChatList>
    get() = Utils.fromJson(JSONArray(response as ArrayList<*>).toString())
}

//data class ChatListResponse(val data: List<ChatList>): BaseResponse()