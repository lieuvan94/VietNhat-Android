package vn.hexagon.vietnhat.repository.mart

import androidx.lifecycle.LiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.data.model.mart.MartDetail
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
class MartDetailViewModel @Inject constructor(
  private val martDetailRepository: MartDetailRepository
) : MVVMBaseViewModel() {

  fun getMartDetail(martId: Int): LiveData<MartDetail> {
    return martDetailRepository.fetchSingleMartDetail(martId, compositeDisposable)
  }

  val martDetailNetworkState: LiveData<NetworkState> by lazy {
    martDetailRepository.getMartDetailNetworkState()
  }

}