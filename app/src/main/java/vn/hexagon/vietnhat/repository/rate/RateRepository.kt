package vn.hexagon.vietnhat.repository.rate

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.rate.RateResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class RateRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Request rating app
     *
     * @param userId
     * @param star
     * @param content
     * @return rate response
     */
    fun requestRate(userId: String, star: String, content: String?): Single<RateResponse> {
        return apiService.requestRating(userId, star, content)
    }
}