package vn.hexagon.vietnhat.ui.list

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.BLANK
import vn.hexagon.vietnhat.data.model.news.NewsDetailResponse
import vn.hexagon.vietnhat.data.model.news.NewsListResponse
import vn.hexagon.vietnhat.data.model.news.SubjectResponse
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.list.PostListRepository
import javax.inject.Inject

/**
 * Created by VuNBT on 9/23/2019.
 */
class PostListViewModel @Inject constructor(private val repository: PostListRepository): MVVMBaseViewModel() {
    /** Post list response */
    val postListResponse = MutableLiveData<ListPostResponse>()
    /** Favourite response */
    val favouriteResponse = MutableLiveData<ListPostResponse>()
    /** Remove favourite response */
    val unFavouriteResponse = MutableLiveData<ListPostResponse>()
    /** News subject response */
    val newSubjectResponse = MutableLiveData<SubjectResponse>()
    /** News list response */
    val newListResponse = MutableLiveData<NewsListResponse>()
    /** News detail response */
    val newDetailResponse = MutableLiveData<NewsDetailResponse>()
    /** Comments response */
    val commentsResponse = MutableLiveData<NewsDetailResponse>()
    // Is top value
    var topValue = MutableLiveData<String>()
    // List is empty
    var listIsEmpty = false
    // User ID
    var userId:String = BLANK
    // User ID
    var postId:String = BLANK

    /**
     * Get data list post
     *
     * @param userId
     * @param serviceId
     * @param index
     * @param number
     */
    fun getDataListPost(userId:String, serviceId:String, index:Int, number:Int) {
        networkState.postValue(NetworkState.LOADING)
        repository.getPostList(userId, serviceId, index, number)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postListResponse.postValue(result)
                    listIsEmpty = result.data.isEmpty() },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Add personal favourite
     *
     * @param userId
     * @param id
     */
    fun addFavouriteRequest(userId: String, id: String) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.getFavourite(userId, id)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    favouriteResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Remove personal favourite
     *
     * @param userId
     * @param id
     */
    fun removeFavouriteRequest(userId: String, id: String) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.removeFavourite(userId, id)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    unFavouriteResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request search for Mart, Support, Ads, Car, Phone, Restaurant,
     * Spa, Support, Translator, Travel, Visa
     *
     * @param userId
     * @param serviceId
     * @param title
     * @param address
     * @param index
     * @param numberPost
     */
    fun getSearchResultCommon(
        userId: String,
        serviceId: String,
        title: String?,
        address: String?,
        index: Int,
        numberPost: Int
    ) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestSearchCommon(userId, serviceId, title, address, index, numberPost)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postListResponse.postValue(result)},
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request search for Mart, Support, Ads
     *
     * @param userId
     * @param title
     * @param index
     * @param numberPost
     */
    fun getSearchResultNew(
        userId: String,
        title: String?,
        subjectId: String?,
        index: Int,
        numberPost: Int
    ) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestSearchNew(userId, title, subjectId, index, numberPost)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postListResponse.postValue(result)},
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request search deliver
     *
     * @param userId
     * @param title
     * @param roadType
     * @param index
     * @param numberPost
     */
    fun requestSearchDeliver(
        userId: String,
        title: String?,
        roadType: String?,
        index: Int,
        numberPost: Int
    ) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestSearchDeliver(
            userId,
            title,
            roadType,
            index,
            numberPost
        )
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postListResponse.postValue(result)},
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get search result of translator
     *
     * @param userId
     * @param title
     * @param index
     * @param numberPost
     * @param address
     */
    fun getSearchResultJob(
        userId: String,
        serviceId: String,
        title: String?,
        index: Int,
        numberPost: Int,
        address: String?
    ) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestSearchCommon(userId, serviceId, title, address, index, numberPost).applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postListResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request subject data
     *
     */
    fun getSubject() {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestNewCategory()
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    newSubjectResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request news list data
     *
     * @param index
     * @param numberPost
     */
    fun requestNewsList(index: Int, numberPost: Int) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestNewsList(index, numberPost)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    newListResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request support list data
     *
     * @param index
     * @param numberPost
     */
    fun requestSupportList(index: Int, numberPost: Int) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestSupportList(index, numberPost)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    newListResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request news detail by id
     *
     */
    fun requestNewsDetail(newsId: String) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.requestNewsDetail(newsId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    newDetailResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Control visibility of top icon
     *
     * @param value 0 is gone, 1 is visible
     */
    fun displayTopIcon(value:String) : Boolean {
        return value != Constant.TOP_UNCHECK
    }

    /**
     * List is empty
     *
     * @return list size
     */
    fun listIsEmpty() :Boolean {
        return postListResponse.value?.data?.isEmpty() ?: true
    }

}