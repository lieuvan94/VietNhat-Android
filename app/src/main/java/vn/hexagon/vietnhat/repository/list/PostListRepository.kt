package vn.hexagon.vietnhat.repository.list

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.news.NewsDetailResponse
import vn.hexagon.vietnhat.data.model.news.NewsListResponse
import vn.hexagon.vietnhat.data.model.news.SubjectResponse
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 * Created by VuNBT on 9/23/2019.
 */
class PostListRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Get data for list post
     *
     * @param userId
     * @param serviceId
     * @param index
     * @param number
     */
    fun getPostList(userId:String, serviceId:String, index:Int, number:Int): Single<ListPostResponse> {
        return apiService.getPostList(userId, serviceId, index, number)
    }

    /**
     * Request favourite
     *
     * @param userId
     * @param id
     */
    fun getFavourite(userId: String, id: String): Single<ListPostResponse> {
        return apiService.getFavourite(userId, id)
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

    /**
     * Request search result for Mart, Support, Ads, Car, Phone, Restaurant,
     * Spa, Support, Translator, Travel, Visa
     *
     * @param userId
     * @param serviceId
     * @param title
     * @param address
     * @param index
     * @param number
     * @return
     */
    fun requestSearchCommon(userId: String, serviceId: String, title: String?, address: String?, index: Int, number: Int): Single<ListPostResponse> {
        return apiService.getCommonSearchResult(userId, serviceId, title, address, index, number)
    }

    /**
     * Request search result for News
     *
     * @param userId
     * @param title
     * @param subjectId
     * @param index
     * @param number
     * @return
     */
    fun requestSearchNew(userId: String, title: String?, subjectId: String?, index: Int, number: Int): Single<ListPostResponse> {
        return apiService.getNewSearchResult(userId, title, subjectId, index, number)
    }

    /**
     * Get search result for deliver
     *
     * @param userId
     * @param title
     * @param roadType
     * @param index
     * @param number
     * @return
     */
    fun requestSearchDeliver(userId: String, title: String?, roadType:String?, index: Int, number: Int): Single<ListPostResponse> {
        return apiService.getSearchDeliver(userId, title, roadType, index, number)
    }

    /**
     * Get data of new category
     *
     * @return request data new category
     */
    fun requestNewCategory(): Single<SubjectResponse> {
        return apiService.getSubject()
    }

    /**
     * Get news list data
     *
     * @param index
     * @param number
     *
     * @return list of news
     */
    fun requestNewsList(index: Int, number: Int): Single<NewsListResponse> {
        return apiService.getNewsList(index, number)
    }

    /**
     * Get support list data
     *
     * @param index
     * @param number
     * @return list of support
     */
    fun requestSupportList(index: Int, number: Int): Single<NewsListResponse> {
        return apiService.getSupportList(index, number)
    }

    /**
     * Get news detail by id
     *
     * @param newsId
     *
     * @return detail of news
     */
    fun requestNewsDetail(newsId: String): Single<NewsDetailResponse> {
        return apiService.getNewsDetail(newsId)
    }
}