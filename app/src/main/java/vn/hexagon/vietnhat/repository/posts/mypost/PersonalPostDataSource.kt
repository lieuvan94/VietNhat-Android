package vn.hexagon.vietnhat.repository.posts.mypost

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.disposables.CompositeDisposable
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.Constant.PAGE
import vn.hexagon.vietnhat.data.model.translator.Translator
import vn.hexagon.vietnhat.data.remote.NetworkService
import vn.hexagon.vietnhat.data.remote.NetworkState
import javax.inject.Inject

/**
 * Created by VuNBT on 9/16/2019.
 */
class PersonalPostDataSource @Inject constructor(
    private val apiService: NetworkService,
    private val compositeDisposable: CompositeDisposable,
    private val userId: String,
    private val serviceId: String
) : PageKeyedDataSource<Int, Translator>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Translator>
    ) {
        // Network state
        networkState.postValue(NetworkState.LOADING)
        // Call Api
        apiService.getListPost(userId, serviceId, 0, 20)
            .applyScheduler()
            .subscribe(
                {result ->
                    // Network state
                    networkState.postValue(NetworkState.LOADED)
                    callback.onResult(result.data, 0, 20, 1, PAGE + 1)},
                {throwable ->
                    // Network state
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Translator>) {
        // Network state
        networkState.postValue(NetworkState.LOADING)
        // Call Api
        apiService.getListPost(userId, serviceId, params.requestedLoadSize, params.requestedLoadSize)
            .applyScheduler()
            .subscribe(
                {result ->
                    // Network state
                    networkState.postValue(NetworkState.LOADED)
                    callback.onResult(result.data, params.key)},
                {throwable ->
                    // Network state
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Translator>
    ) {
    }

    /*override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Translator>
    ) {
        DebugLog.e("Loading Rang " + 0 + " Count " + params.requestedLoadSize)
        // Loading state
        // Call api
        apiService.getListPost(userId, serviceId, 0, params.requestedLoadSize)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    callback.onResult(result.data)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }

            ).addToCompositeDisposable(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Translator>) {
        DebugLog.e("Loading Rang " + params.key.toInt()  + " Count " + params.requestedLoadSize)
        // Loading state
        networkState.postValue(NetworkState.LOADING)
        // Call api
        apiService.getListPost(userId, serviceId, params.requestedLoadSize.plus(5),  params.key.toInt().minus(5) )
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    callback.onResult(result.data)
                    println(result.data.size)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Translator>) {
        // Nothing
    }

    override fun getKey(item: Translator): String {
        return item.id
    }*/

    // Network state
    val networkState = MutableLiveData<NetworkState>()

}