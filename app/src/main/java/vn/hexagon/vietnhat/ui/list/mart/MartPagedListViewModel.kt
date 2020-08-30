package vn.hexagon.vietnhat.ui.list.mart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.data.model.mart.Mart
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.mart.MartPagedListRepository
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
 * Create on：2019-08-30
 * =====================================================
 */
class MartPagedListViewModel @Inject constructor(
    private val martPagedListRepository: MartPagedListRepository)
    : MVVMBaseViewModel() {

    val martPagedList:LiveData<PagedList<Mart>> by lazy {
        martPagedListRepository.fetchLiveMartPagedList(compositeDisposable)
    }

    /**
     * Check list is empty or not
     */
    fun listIsEmpty(): Boolean {
        return martPagedList.value?.isEmpty() ?: true
    }
}