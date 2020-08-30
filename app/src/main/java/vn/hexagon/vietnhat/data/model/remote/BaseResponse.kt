package vn.hexagon.vietnhat.data.model.remote

import com.google.gson.annotations.SerializedName
import vn.hexagon.vietnhat.constant.Constant

/**
 * Created by NhamVD on 2019-10-04.
 */
open class BaseResponse {
  @SerializedName("data")
  val response: Any? = null
  val errorId: Int = -1
  val message: String = ""

  fun isSuccess(): Boolean = errorId == Constant.SUCCESS_CODE

  //Process convert error
//  fun getError(): Error = Constant.getError(errorId)
}