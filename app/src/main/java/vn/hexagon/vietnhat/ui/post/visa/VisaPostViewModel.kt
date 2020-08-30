package vn.hexagon.vietnhat.ui.post.visa

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
class VisaPostViewModel @Inject constructor(private val repository: PostCreateRepository): MVVMBaseViewModel() {
    // Visa response
    val visaPostResponse = MutableLiveData<PostResponse>()
    // Visa edit response
    val visaEditPostResponse = MutableLiveData<PostResponse>()
    // Visa detail response
    val visaDetailResponse = MutableLiveData<PostDetailResponse>()
    // Request file
    private var requestFile = ArrayList<MultipartBody.Part?>()

    var title = Constant.BLANK
    var content = Constant.BLANK
    var price = Constant.BLANK
    var phone = Constant.BLANK
    var address = Constant.BLANK
    var note = Constant.BLANK

    /**
     * Request data
     *
     * @param userId
     * @param serviceId
     * @param title
     * @param content
     * @param isTop
     * @param imgSwipeList
     */
    fun requestDataPost(userId:String, serviceId:String, title: String,
                         content: String, price: String, address: String, note: String, phone: String, isTop:String, imgSwipeList: ArrayList<String>) {

        // Add mutable map, upload fields data
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("service_id", requestBody(serviceId))
            put("title", requestBody(title))
            put("content", requestBody(content))
            put("price", requestBody(price))
            put("address", requestBody(address))
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
        // Request data
        repository.sendDataPost(params, coverImages, requestFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    visaPostResponse.postValue(result) },
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
     * @param phone
     * @param isTop
     * @param imgSwipeList
     */
    fun editDataPost(userId:String, postId:String, phone: String, isTop:String, imgSwipeList: ArrayList<String>) {

        // Add mutable map, upload fields data
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("post_id", requestBody(postId))
            put("title", requestBody(title))
            put("content", requestBody(content))
            put("price", requestBody(price))
            put("address", requestBody(address))
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
                    visaEditPostResponse.postValue(result) },
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
                    visaDetailResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }
}