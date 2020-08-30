package vn.hexagon.vietnhat.repository.fav

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-26 
 */
class FavouriteRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Get favourite list
     *
     * @param userId
     * @param serviceId
     * @return fav list
     */
    fun getFavList(userId:String, serviceId:String?): Single<ListPostResponse> {
        return apiService.getFavList(userId, serviceId)
    }

    /**
     * Request favourite
     *
     * @param userId
     * @param id
     */
    fun removeFavourite(userId: String, id: String): Single<ListPostResponse> {
        return apiService.removeFavourite(userId, id)
    }
}