package vn.hexagon.vietnhat.repository.posts.common

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.job.JobResponse
import vn.hexagon.vietnhat.data.model.news.SubjectResponse
import vn.hexagon.vietnhat.data.model.product.ProductResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 * Created by VuNBT on 9/15/2019.
 */
class PostCreateRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * send Data Post to server
     *
     * @param map
     * @param img
     * @return data post
     */
    fun sendDataPost(map: Map<String, RequestBody>, img: List<@JvmSuppressWildcards RequestBody>, imgFiles: ArrayList<MultipartBody.Part?>): Single<PostResponse> {
        return apiService.createNormalPost(map, img, imgFiles)
    }

    /**
     * send Data Post to server
     *
     * @param map
     * @param img
     * @param imgFiles
     * @return data post
     */
    fun editDataPost(map: Map<String, RequestBody>, img: List<@JvmSuppressWildcards RequestBody>, imgFiles: ArrayList<MultipartBody.Part?>): Single<PostResponse> {
        return apiService.requestEditPost(map, img, imgFiles)
    }

    /**
     * Request post with products
     *
     * @param map
     * @param products
     * @param img
     * @param coverImg
     * @return
     */
    fun sendDataPostObj(
        map: Map<String, RequestBody>,
        products: List<RequestBody>, img: List<MultipartBody.Part?>,
        coverImages: List<RequestBody>, coverImg: List<MultipartBody.Part?>
    ): Single<PostResponse> {
        return apiService.createPostWithProduct(map, products, img, coverImages, coverImg)
    }

    /**
     * send Product data to server
     *
     * @param map
     * @param img
     * @return
     */
    fun editPostProductRequest(map: Map<String, RequestBody>,
                               products: List<RequestBody>, img: ArrayList<MultipartBody.Part?>,
                               coverImages: List<RequestBody>, coverImg: ArrayList<MultipartBody.Part?>): Single<PostResponse> {
        return apiService.editPostProduct(map, products, img, coverImages, coverImg)
    }

    /**
     * Get product list
     *
     * @param postId
     * @return product list
     */
    fun getProductList(postId: String): Single<ProductResponse> {
        return apiService.getProductList(postId)
    }

    /**
     * Get detail post info
     *
     * @param userId
     * @param postId
     * @return
     */
    fun getDetailPost(userId:String, postId:String): Single<PostDetailResponse> {
        return apiService.getDetailPost(userId, postId)
    }

    /**
     * Get data of job type
     *
     * @return request data job type
     */
    fun requestJobType(): Single<JobResponse> {
        return apiService.getJobType()
    }

    /**
     * Get data of new category
     *
     * @return request data new category
     */
    fun requestNewCategory(): Single<SubjectResponse> {
        return apiService.getSubject()
    }
}