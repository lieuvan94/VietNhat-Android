package vn.hexagon.vietnhat.repository.mart

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.data.model.mart.Mart
import vn.hexagon.vietnhat.data.remote.NetworkService

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
class MartDataSourceFactory(
    private val apiService: NetworkService,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Mart>() {

    // Data Source
    val martLiveDataSource = MutableLiveData<MartDataSource>()

    override fun create(): DataSource<Int, Mart> {
        val martDataSource = MartDataSource(apiService, compositeDisposable)
        martLiveDataSource.postValue(martDataSource)
        return martDataSource
    }
}