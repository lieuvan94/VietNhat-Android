package vn.hexagon.vietnhat.repository.home

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.banner.BannerResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class BannerRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Get app banner
     *
     * @return app banner
     */
    fun getHomeBanner(): Single<BannerResponse> {
        return apiService.getBanner()
    }
}