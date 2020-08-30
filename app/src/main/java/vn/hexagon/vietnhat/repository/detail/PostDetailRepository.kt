package vn.hexagon.vietnhat.repository.detail

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.comment.CommentResponse
import vn.hexagon.vietnhat.data.model.job.JobResponse
import vn.hexagon.vietnhat.data.model.news.SubjectResponse
import vn.hexagon.vietnhat.data.model.product.ProductResponse
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/**
 *
//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//                    Pray for no Bugs
 * =====================================================
 * Name：VuNBT
 * Create on：2019-09-29
 * =====================================================
 */
class PostDetailRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Request detail post
     */
    fun getDetailPost(userId:String, postId:String): Single<PostDetailResponse> {
        return apiService.getDetailPost(userId, postId)
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
     * Request get job type
     *
     * @return job type Array<>
     */
    fun getJobType(): Single<JobResponse> {
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

    /**
     * Request comment by user
     *
     * @param userId
     * @param postId
     * @param content
     * @return
     */
    fun requestComment(userId: String, postId: String, content: String): Single<CommentResponse> {
        return apiService.requestComment(userId, postId, content)
    }
}