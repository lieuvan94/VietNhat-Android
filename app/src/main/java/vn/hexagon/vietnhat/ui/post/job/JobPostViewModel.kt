package vn.hexagon.vietnhat.ui.post.job

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
import vn.hexagon.vietnhat.data.model.job.JobResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.posts.common.PostCreateRepository
import java.io.File
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-19 
 */
class JobPostViewModel @Inject constructor(private val repository: PostCreateRepository): MVVMBaseViewModel() {
    // Job response
    val jobPostResponse = MutableLiveData<PostResponse>()
    // Job edit response
    val jobEditResponse = MutableLiveData<PostResponse>()
    // Job detail response
    val jobDetailResponse = MutableLiveData<PostDetailResponse>()
    // Job type response
    val jobTypeResponse = MutableLiveData<JobResponse>()
    // Job category response
    val jobCategoryResponse = MutableLiveData<JobResponse>()
    // Request file
    private var requestFile = ArrayList<MultipartBody.Part?>()

    var title = Constant.BLANK
    var address = Constant.BLANK
    var category = Constant.BLANK
    var phone = Constant.BLANK
    var salary = Constant.BLANK
    var content = Constant.BLANK
    var note = Constant.BLANK

    /**
     * Request data
     *
     * @param userId
     * @param serviceId
     * @param title
     * @param jobTypeId
     * @param jobCategory
     * @param salaryType
     * @param address
     * @param phone
     * @param salary
     * @param content
     * @param note
     * @param isTop
     * @param imgSwipeList
     */
    fun requestDataPost(userId:String, serviceId:String, title: String, jobTypeId:String, jobCategory: String, salaryType: String,
                        address: String, phone: String, salary: String, content: String, note: String, isTop:String, imgSwipeList: ArrayList<String>) {

        // Add mutable map, upload fields data
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("service_id", requestBody(serviceId))
            put("title", requestBody(title))
            put("job_type_id", requestBody(jobTypeId))
            put("job_category", requestBody(jobCategory))
            put("address", requestBody(address))
            put("phone", requestBody(phone))
            put("salary", requestBody(salary))
            put("salary_type", requestBody(salaryType))
            put("content", requestBody(content))
            put("note", requestBody(note))
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
        repository.sendDataPost(params, coverImages, requestFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    jobPostResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request data for job type
     *
     */
    fun requestJobTypeData() {
        // Request data
        repository.requestJobType()
            .applyScheduler()
            .subscribe(
                {result -> jobTypeResponse.postValue(result) },
                {throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request update data
     *
     * @param userId
     * @param postId
     * @param phone
     * @param jobTypeId
     * @param jobCategory
     * @param salaryType
     * @param isTop
     * @param imgSwipeList
     */
    fun editDataPost(userId:String, postId:String, phone: String, jobTypeId: String, jobCategory: String, salaryType: String, isTop:String, imgSwipeList: ArrayList<String>) {

        // Add mutable map, upload fields data
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("post_id", requestBody(postId))
            put("title", requestBody(title))
            put("job_type_id", requestBody(jobTypeId))
            put("job_category", requestBody(jobCategory))
            put("address", requestBody(address))
            put("phone", requestBody(phone))
            put("salary", requestBody(salary))
            put("salary_type", requestBody(salaryType))
            put("content", requestBody(content))
            put("note", requestBody(note))
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
        // Request data
        networkState.postValue(NetworkState.LOADING)
        repository.editDataPost(params, coverImages, requestFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    jobEditResponse.postValue(result) },
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
        networkState.postValue(NetworkState.LOADING)
        repository.getDetailPost(userId, postId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    jobDetailResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }
}