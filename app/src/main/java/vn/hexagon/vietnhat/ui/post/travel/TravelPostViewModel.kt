package vn.hexagon.vietnhat.ui.post.travel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.detail.Images
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.posts.common.PostCreateRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by VuNBT on 9/22/2019.
 */
class TravelPostViewModel @Inject constructor(private val repository: PostCreateRepository): MVVMBaseViewModel() {
    // Travel response
    val travelPostResponse = MutableLiveData<PostResponse>()
    // Travel edit response
    val travelEditPostResponse = MutableLiveData<PostResponse>()
    // Travel detail response
    val travelDetailResponse = MutableLiveData<PostDetailResponse>()
    // Request file
    private var requestFile = ArrayList<MultipartBody.Part?>()

    var title = Constant.BLANK
    var content = Constant.BLANK
    var price = Constant.BLANK
    var note = Constant.BLANK
    var phone = Constant.BLANK
    var img:String? = Constant.BLANK

    /**
     * Request data
     *
     * @param userId
     * @param serviceId
     * @param title
     * @param content
     * @param price
     * @param note
     * @param phone
     * @param isTop
     * @param imgSwipeList
     */
    fun requestTravelDataPost(userId:String, serviceId:String, title: String,
                         content: String, price: String, note: String, phone: String, isTop:String, imgSwipeList: ArrayList<String>) {

        // Add mutable map, upload fields data
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("service_id", requestBody(serviceId))
            put("title", requestBody(title))
            put("content", requestBody(content))
            put("price", requestBody(price))
            put("note", requestBody(note))
            put("phone", requestBody(phone))
            put("is_top", requestBody(isTop))
        }

        /////////////////// Image cover //////////////////////
        // Get image name
        val coverImages = ArrayList<RequestBody>()
        val imagesListObject =  ArrayList<Images>()
        imgSwipeList.forEach {
            val imageName = File(it).name
            DebugLog.e("Image name upload: $imageName")
            val images = Images(null, imageName)
            imagesListObject.add(images)
            // Add new image for upload
            requestFile.add(prepareImageCoverForRequest("img[]", File(it)))
        }
        // Upload cover images JSON Object
        coverImages.clear()
        coverImages.add(requestBody(Gson().toJson(imagesListObject).toString()))

        // Request data
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.sendDataPost(params, coverImages, requestFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    travelPostResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request update data
     *
     * @param userId
     * @param postId
     * @param isTop
     * @param imgSwipeList
     */
    fun editDataPost(userId:String, postId:String, phone: String, isTop:String, imgSwipeList: ArrayList<String>) {

        // Add mutable map, upload fields data
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("title", requestBody(title))
            put("post_id", requestBody(postId))
            put("content", requestBody(content))
            put("price", requestBody(price))
            put("note", requestBody(note))
            put("phone", requestBody(phone))
            put("is_top", requestBody(isTop))
        }
        /////////////////// Image cover //////////////////////
        // Get image name
        val coverImages = ArrayList<RequestBody>()
        val imagesListObject =  ArrayList<Images>()
        imgSwipeList.forEach {
            val imageName = File(it).name
            DebugLog.e("Image name upload: $imageName")
            val images = Images(null, imageName)
            imagesListObject.add(images)
        }
        // Upload cover images JSON Object
        coverImages.clear()
        coverImages.add(requestBody(Gson().toJson(imagesListObject).toString()))
        // Upload cover image file
        for (i in 0 until imgSwipeList.size) {
            // Check image uploaded then skip it
            val regex = imgSwipeList[i].contains("chapp.vn")
            if (regex) continue
            // Add new image for upload
            requestFile.add(prepareImageCoverForRequest("img[]", File(imgSwipeList[i])))
        }
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.editDataPost(params, coverImages, requestFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    travelEditPostResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get detail post
     *
     * @param userId
     * @param postId
     */
    fun getDetailPost(userId:String, postId: String) {
        // Start loading
        networkState.postValue(NetworkState.LOADING)
        // Send request
        repository.getDetailPost(userId, postId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    travelDetailResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }
}