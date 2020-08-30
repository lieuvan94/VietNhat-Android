package vn.hexagon.vietnhat.ui

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.auth.FcmTokenResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-10-30 
 */
class MainRepository @Inject constructor(private val apiService: NetworkService)  {

    /**
     * Request update fcm token
     *
     * @param userId
     * @param token
     * @return
     */
    fun updateFcmToken(userId: String, token: String): Single<FcmTokenResponse> {
        return apiService.requestUpdateToken(userId, token)
    }
}