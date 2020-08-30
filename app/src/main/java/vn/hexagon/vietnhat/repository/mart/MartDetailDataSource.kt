package vn.hexagon.vietnhat.repository.mart

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.APIConstant
import vn.hexagon.vietnhat.data.model.mart.MartDetail
import vn.hexagon.vietnhat.data.remote.NetworkService
import vn.hexagon.vietnhat.data.remote.NetworkState
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
 * Create on：2019-09-01
 * =====================================================
 */
class MartDetailDataSource @Inject constructor(
  private val apiService: NetworkService,
  private val compositeDisposable: CompositeDisposable
) {
  val networkState = MutableLiveData<NetworkState>()
  val martDetailResponse = MutableLiveData<MartDetail>()

  /**
   * Fetch data for mart detail
   * @param martId: Mart ID
   */
  fun fetchMartDetail(martId: Int) {
    networkState.postValue(NetworkState.LOADING)
    apiService.getMartDetail(martId, APIConstant.API_KEY)
      .applyScheduler()
      .subscribe({ martDetail ->
        martDetailResponse.postValue(martDetail)
        networkState.postValue(NetworkState.LOADED)
      }, {
        networkState.postValue(NetworkState.ERROR)
        DebugLog.e("MartDetailDataSource: ${it.message.toString()}")
      }).addToCompositeDisposable(compositeDisposable)
  }
}