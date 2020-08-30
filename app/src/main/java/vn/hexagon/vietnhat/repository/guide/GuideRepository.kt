package vn.hexagon.vietnhat.repository.guide

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.guide.GuideDetailResponse
import vn.hexagon.vietnhat.data.model.guide.GuideResponse
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
 * Create on：2019-09-29
 * =====================================================
 */
class GuideRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Get guide response
     *
     * @return list guide data
     */
    fun getGuide() : Single<GuideResponse> {
        return apiService.getGuide()
    }


    /**
     * Get guide detail
     *
     * @param id
     * @return guide detail data
     */
    fun getGuideDetail(id:String): Single<GuideDetailResponse> {
        return apiService.getGuideDetail(id)
    }
}