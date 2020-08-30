package vn.hexagon.vietnhat.ui.favourite

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.fav.FavouriteRepository
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-26 
 */
class FavoriteViewModel @Inject constructor(private val repository: FavouriteRepository): MVVMBaseViewModel() {

    // Fav response
    val favouriteResponse = MutableLiveData<ListPostResponse>()
    // Fav remove
    val removeFavouriteResponse = MutableLiveData<ListPostResponse>()

    /**
     * Get favourite list
     *
     * @param userId
     * @param serviceId (maybe no need, only use for filter)
     */
    fun getFavList(userId: String, serviceId: String?) {
        networkState.postValue(NetworkState.LOADING)
        repository.getFavList(userId, serviceId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    favouriteResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Remove personal favourite
     *
     * @param userId
     * @param id
     */
    fun removeFavouriteRequest(userId: String, id: String) {
        repository.removeFavourite(userId, id)
            .applyScheduler()
            .subscribe(
                { result -> removeFavouriteResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }
}