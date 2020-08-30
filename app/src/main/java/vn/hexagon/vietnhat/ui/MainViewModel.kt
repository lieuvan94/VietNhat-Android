package vn.hexagon.vietnhat.ui

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.auth.FcmTokenResponse
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-08-17.
 */
class MainViewModel @Inject constructor(private val repository: MainRepository): MVVMBaseViewModel() {
    // Token response
    val tokenResponse = MutableLiveData<FcmTokenResponse>()

    /**
     * Request update token fcm
     *
     * @param userId
     * @param token
     */
    fun requestUpdateToken(userId: String, token: String) {
        repository.updateFcmToken(userId, token)
            .applyScheduler()
            .subscribe(
                { result -> tokenResponse.postValue(result)},
                { throwable -> DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }
}