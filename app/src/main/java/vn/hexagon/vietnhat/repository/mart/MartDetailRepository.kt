package vn.hexagon.vietnhat.repository.mart

import androidx.lifecycle.LiveData
import io.reactivex.disposables.CompositeDisposable
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
class MartDetailRepository @Inject constructor(private val apiService: NetworkService) {
  private lateinit var martDetailDataSource: MartDetailDataSource

  fun fetchSingleMartDetail(
    martId: Int,
    compositeDisposable: CompositeDisposable
  ): LiveData<MartDetail> {
    martDetailDataSource = MartDetailDataSource(apiService, compositeDisposable)
    martDetailDataSource.fetchMartDetail(martId)
    return martDetailDataSource.martDetailResponse
  }

  fun getMartDetailNetworkState(): LiveData<NetworkState> {
    return martDetailDataSource.networkState
  }
}