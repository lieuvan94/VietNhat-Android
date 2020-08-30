package vn.hexagon.vietnhat.data.model.remote

import org.json.JSONObject
import vn.hexagon.vietnhat.base.utils.Utils.fromJson

class ChatDetailResponse : BaseResponse() {
  val data: ChatDetail
    get() = fromJson(JSONObject(response as MutableMap<*, *>).toString())
}
