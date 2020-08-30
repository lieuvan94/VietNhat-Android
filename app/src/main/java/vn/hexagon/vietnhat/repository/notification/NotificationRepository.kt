package vn.hexagon.vietnhat.repository.notification

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.notification.NotifyResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class NotificationRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Get notification list
     *
     * @param userId
     * @return
     */
    fun getNotifyList(userId:String): Single<NotifyResponse> {
        return apiService.getListNotifications(userId)
    }
}