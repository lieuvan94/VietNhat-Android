package vn.hexagon.vietnhat.ui.detail

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.comment.CommentResponse
import vn.hexagon.vietnhat.data.model.job.JobResponse
import vn.hexagon.vietnhat.data.model.news.SubjectResponse
import vn.hexagon.vietnhat.data.model.product.ProductResponse
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.detail.PostDetailRepository
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
class PostDetailViewModel @Inject constructor(private val repository: PostDetailRepository): MVVMBaseViewModel() {
    // Detail response
    val detailPostResponse = MutableLiveData<PostDetailResponse>()
    // Product list response
    val productListResponse = MutableLiveData<ProductResponse>()
    /** Favourite response */
    val favouriteResponse = MutableLiveData<ListPostResponse>()
    /** Remove favourite response */
    val unFavouriteResponse = MutableLiveData<ListPostResponse>()
    /** Job type response */
    val jobTypeResponse = MutableLiveData<JobResponse>()
    /** Subject response */
    val subjectResponse = MutableLiveData<SubjectResponse>()
    /** Comment request response */
    val commentResponse = MutableLiveData<CommentResponse>()

    /**
     * Request data userInfo and PostDetail
     *
     * @param userId
     * @param postId
     */
    fun getDetailPost(userId: String, postId: String) {
        networkState.postValue(NetworkState.LOADING)
        repository.getDetailPost(userId, postId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    detailPostResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get product list
     *
     * @param postId
     */
    fun getProductListPost(postId:String) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.getProductList(postId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    productListResponse.postValue(result)},
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request add favourite
     *
     * @param userId
     * @param postId
     */
    fun addFavourite(userId: String, postId: String) {
        repository.getFavourite(userId, postId)
            .applyScheduler()
            .subscribe(
                { result -> favouriteResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request remove favourite
     *
     * @param userId
     * @param postId
     */
    fun removeFavourite(userId: String, postId: String) {
        repository.removeFavourite(userId, postId)
            .applyScheduler()
            .subscribe(
                { result -> unFavouriteResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get job type
     *
     */
    fun requestJobType() {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.getJobType()
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    jobTypeResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get news category
     *
     */
    fun requestNewsCategory() {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestNewCategory()
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    subjectResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request comment by user
     *
     * @param userId
     * @param postId
     */
    fun requestComment(userId: String, postId: String, content: String) {
        repository.requestComment(userId, postId, content)
            .applyScheduler()
            .subscribe(
                { result -> commentResponse.postValue(result)},
                { throwable -> DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }
}