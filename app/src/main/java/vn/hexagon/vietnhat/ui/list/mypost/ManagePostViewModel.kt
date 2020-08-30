package vn.hexagon.vietnhat.ui.list.mypost

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.posts.mypost.ManagePostRepository
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
class ManagePostViewModel @Inject constructor(private val repository: ManagePostRepository) :
    MVVMBaseViewModel() {
    // Personal post list response
    val personalPostResponse = MutableLiveData<ListPostResponse>()
    // Remove post response
    val personalPostRemoveResponse = MutableLiveData<ListPostResponse>()

    /**
     * Request personal post list
     *
     * @param userId
     * @param serviceId
     * @param index
     * @param number
     */
    fun getPersonalPostList(userId: String, serviceId: String?, index: Int, number: Int) {
        networkState.postValue(NetworkState.LOADING)
        repository.getPersonalPost(userId, serviceId, index, number)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    personalPostResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Remove personal post
     *
     * @param userId
     * @param postId
     */
    fun requestRemovePost(userId: String, postId: String) {
        networkState.postValue(NetworkState.LOADING)
        repository.removePersonalPost(userId, postId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    personalPostRemoveResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * List is empty
     *
     * @return list size
     */
    fun listIsEmpty() :Boolean {
        return personalPostResponse.value?.data?.isEmpty() ?: true
    }
}