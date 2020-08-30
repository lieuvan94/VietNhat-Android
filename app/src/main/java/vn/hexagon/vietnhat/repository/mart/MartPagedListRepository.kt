package vn.hexagon.vietnhat.repository.mart

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.constant.Constant.POST_PER_PAGE
import vn.hexagon.vietnhat.data.model.mart.Mart
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
 * Create on：2019-08-30
 * =====================================================
 */
class MartPagedListRepository @Inject constructor(private val apiService: NetworkService) {
    lateinit var martPagedList:LiveData<PagedList<Mart>>
    lateinit var martDataSourceFactory:MartDataSourceFactory

    /**
     * Fetch Mart list with PagedList & LiveData
     */
    fun fetchLiveMartPagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Mart>> {
        martDataSourceFactory = MartDataSourceFactory(apiService, compositeDisposable)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
        martPagedList = LivePagedListBuilder(martDataSourceFactory, config).build()
        return martPagedList
    }

    /**
     * Get network state status
     */
    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<MartDataSource, NetworkState>(
            martDataSourceFactory.martLiveDataSource, MartDataSource::networkState)
    }
}