package vn.hexagon.vietnhat.repository.posts.mypost

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 *
//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//                    Pray for no Bugs
 * =====================================================
 * Name：VuNBT
 * Create on：2019-10-05
 * =====================================================
 */
class ManagePostRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Get post list personal
     *
     * @param userId
     * @param serviceId
     * @param index
     * @param number
     * @return
     */
    fun getPersonalPost(userId:String, serviceId:String?, index:Int, number:Int): Single<ListPostResponse> {
        return apiService.getPersonalPost(userId, serviceId, index, number)
    }

    /**
     * Remove personal post
     *
     * @param userId
     * @param postId
     */
    fun removePersonalPost(userId: String, postId: String): Single<ListPostResponse> {
        return apiService.removePost(userId, postId)
    }
}