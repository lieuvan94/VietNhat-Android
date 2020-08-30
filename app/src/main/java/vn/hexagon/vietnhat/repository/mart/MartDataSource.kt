package vn.hexagon.vietnhat.repository.mart

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import io.reactivex.schedulers.Schedulers
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.APIConstant
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
 * Create on：2019-08-29
 * =====================================================
 */
class MartDataSource @Inject constructor(
    private val apiService: NetworkService,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Mart>() {

    // Page number
    private val FIRST_PAGE = 1
    // Network state
    val networkState = MutableLiveData<NetworkState>()

    /**
     * Display init page
     * This method will show first
     */
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Mart>
    ) {
        // Send status progress loading to state
        networkState.postValue(NetworkState.LOADING)
        apiService.getMart(FIRST_PAGE, APIConstant.API_KEY)
            .applyScheduler()
            .subscribe({
                // Send status progress loaded to state
                networkState.postValue(NetworkState.LOADED)
                // Display first page with page numb = 1 & next page + 1
                callback.onResult(it.movies, null, FIRST_PAGE + 1)
            },
                {
                    // Send status progress load failed to state
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(it.message.toString())
                }).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Trigger load more
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Mart>) {
        // Send status progress loading to state
        networkState.postValue(NetworkState.LOADING)
        apiService.getMart(params.key, APIConstant.API_KEY)
            .subscribeOn(Schedulers.io())
            .subscribe({
                // Send status progress loaded to state
                networkState.postValue(NetworkState.LOADED)
                // Display first page with page numb = 1 & next page + 1
                callback.onResult(it.movies, params.key + 1)
            },
                {
                    // Send status progress load failed to state
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(it.message.toString())
                }).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Previous page(unused)
     */
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Mart>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}