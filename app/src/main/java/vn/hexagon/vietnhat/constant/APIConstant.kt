package vn.hexagon.vietnhat.constant

/**
 * Created by NhamVD on 2019-07-30.
 */
object APIConstant {
    const val TIMEOUT_IN_MS = 30000L
    /** Base URL */
    const val BASE_SERVER_URL = "http://chapp.vn/vnjapan/api/"
//    const val BASE_SERVER_URL = "https://api.themoviedb.org/3/"
    /** Only for test*/
    const val API_KEY = "2f5c0a189c992ac7896d296f837ab5e1"
    const val POST_PATH_URL = "https://image.tmdb.org/t/p/w500"

    /** Socket URL **/
    const val SOCKET_URL = "http://45.119.82.138:4001"
    const val SOCKET_EVENT_SEND_MESSAGE ="vnj_send_chat"
    const val SOCKET_EVENT_RECEIVE_MESSAGE ="vnj_chat"

}