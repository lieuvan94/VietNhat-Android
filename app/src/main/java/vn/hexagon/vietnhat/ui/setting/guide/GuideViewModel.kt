package vn.hexagon.vietnhat.ui.setting.guide

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.guide.GuideDetailResponse
import vn.hexagon.vietnhat.data.model.guide.GuideResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.guide.GuideRepository
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
class GuideViewModel @Inject constructor(private val repository: GuideRepository): MVVMBaseViewModel() {
    // Guide response
    val guideResponse = MutableLiveData<GuideResponse>()
    // Guide detail response
    val guideDetailResponse = MutableLiveData<GuideDetailResponse>()

    /**
     * Get Guide list
     */
    fun getGuideList() {
        networkState.postValue(NetworkState.LOADING)
        repository.getGuide()
            .applyScheduler()
            .subscribe(
                { result ->
                    guideResponse.postValue(result)
                    networkState.postValue(NetworkState.LOADED)
                },
                { throwable ->
                    DebugLog.e(throwable.message.toString())
                    networkState.postValue(NetworkState.ERROR)
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get guide detail
     *
     * @param id guideId
     */
    fun getGuideDetail(id: String) {
        networkState.postValue(NetworkState.LOADING)
        repository.getGuideDetail(id)
            .applyScheduler()
            .subscribe(
                { result ->
                    guideDetailResponse.postValue(result)
                    networkState.postValue(NetworkState.LOADED)
                },
                { throwable ->
                    DebugLog.e(throwable.message.toString())
                    networkState.postValue(NetworkState.ERROR)
                }
            ).addToCompositeDisposable(compositeDisposable)
    }
}