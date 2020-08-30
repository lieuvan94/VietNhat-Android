package vn.hexagon.vietnhat.ui.setting.notification

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.notification.NotifyResponse
import vn.hexagon.vietnhat.repository.notification.NotificationRepository
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class NotifyViewModel @Inject constructor(private val repository: NotificationRepository): MVVMBaseViewModel() {
    // Notifications list response
    val notifyListResponse = MutableLiveData<NotifyResponse>()

    /**
     * Get notification list
     *
     * @param userId
     */
    fun getNotifyList(userId: String) {
        repository.getNotifyList(userId)
            .applyScheduler()
            .subscribe(
                { result -> notifyListResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }
}