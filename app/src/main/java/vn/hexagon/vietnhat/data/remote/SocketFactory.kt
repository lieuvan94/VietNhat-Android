package vn.hexagon.vietnhat.data.remote

import io.socket.client.IO
import io.socket.client.Manager
import vn.hexagon.vietnhat.constant.APIConstant
import java.net.URI

/**
 * Created by NhamVD on 2019-09-24.
 */
object SocketFactory {
  private val socketManager: Manager by lazy {
    Manager(URI(APIConstant.SOCKET_URL))
  }

  fun socketService() = SocketService(IO.socket(APIConstant.SOCKET_URL))
}