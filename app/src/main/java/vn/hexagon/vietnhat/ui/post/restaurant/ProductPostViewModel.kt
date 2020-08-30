package vn.hexagon.vietnhat.ui.post.restaurant

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
import vn.hexagon.vietnhat.data.model.product.Product
import vn.hexagon.vietnhat.data.model.product.ProductResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.posts.common.PostCreateRepository
import java.io.File
import javax.inject.Inject


/*
 * Create by VuNBT on 2019-10-02 
 */
class ProductPostViewModel @Inject constructor(private val repository: PostCreateRepository): MVVMBaseViewModel() {
    // Create post with product response
    val postProductResponse = MutableLiveData<PostResponse>()
    // Detail post response
    val detailPostResponse = MutableLiveData<PostDetailResponse>()
    // Product list response
    val productPostResponse = MutableLiveData<ProductResponse>()
    // Title
    var title = Constant.BLANK
    // Address
    var address = Constant.BLANK
    // Phone
    var phone = Constant.BLANK
    // Note
    var note = Constant.BLANK
    // Is top
    var isTop = Constant.BLANK
    // Content
    var content = Constant.BLANK
    // Cover image
    var img:String? = Constant.BLANK
    // Request file
    private var imgList = ArrayList<MultipartBody.Part?>()
    // Product list object
    private var productList = ArrayList<RequestBody>()
    // Request cover image file
    private var requestCoverFile = ArrayList<MultipartBody.Part?>()

    /**
     * Send data with product info
     *
     * @param userId
     * @param serviceId
     * @param isTop
     * @param products
     */
    fun sendDataPost(
        userId: String, serviceId: String, phone: String, isTop: String, products: ArrayList<Product>, imgSwipeList: ArrayList<String>) {
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("service_id", requestBody(serviceId))
            put("title", requestBody(title))
            put("address", requestBody(address))
            put("phone", requestBody(phone))
            put("note", requestBody(note))
            put("is_top", requestBody(isTop))
            put("content", requestBody(content))
        }
        // Rewrite product img name (must match with image file name)
        val newProducts = ArrayList<Product>()
        products.forEach {
            val imageName = File(it.img).name
            DebugLog.e("Image name: $imageName")
            val product = Product(it.name, it.price, imageName)
            newProducts.add(product)
        }
        productList.clear()
        productList.add(requestBody(Gson().toJson(newProducts).toString()))

        // Get products
        for (i in 0 until products.size) {
            // Add new image for upload
            imgList.add(prepareImageForRequest("img_product[]", File(products[i].img)))
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
            requestCoverFile.add(prepareImageCoverForRequest("img[]", File(it)))
        }
        // Upload cover images JSON Object
        coverImages.clear()
        coverImages.add(requestBody(Gson().toJson(imagesListObject).toString()))
        // Send request data
        networkState.postValue(NetworkState.LOADING)
        repository.sendDataPostObj(params, productList, imgList, coverImages, requestCoverFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postProductResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Prepare data before editing post
     *
     * @param userId
     * @param postId
     */
    fun getPostInfo(userId: String, postId:String) {
        networkState.postValue(NetworkState.LOADING)
            repository.getDetailPost(userId, postId)
                .applyScheduler()
                .subscribe(
                    { result ->
                        networkState.postValue(NetworkState.LOADED)
                        detailPostResponse.postValue(result)},
                    { throwable ->
                        networkState.postValue(NetworkState.ERROR)
                        DebugLog.e(throwable.message.toString()) }
                ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get product list
     *
     * @param postId
     */
    fun getProductList(postId: String) {
        networkState.postValue(NetworkState.LOADING)
        repository.getProductList(postId)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    productPostResponse.postValue(result)},
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request data after update
     *
     * @param userId
     * @param postId
     * @param phone
     * @param isTop
     * @param products
     */
    fun requestEditPost(userId: String, postId:String, phone: String, isTop: String, products: ArrayList<Product>, imgSwipeList: ArrayList<String>) {
        // Product list object
        val productList = ArrayList<RequestBody>()
        val params = mutableMapOf<String, RequestBody>()
        params.apply {
            put("user_id", requestBody(userId))
            put("post_id", requestBody(postId))
            put("title", requestBody(title))
            put("address", requestBody(address))
            put("phone", requestBody(phone))
            put("content", requestBody(content))
            put("note", requestBody(note))
            put("is_top", requestBody(isTop))
        }

        /////////////////// Products //////////////////////
        // Rewrite product img name (must match with image file name)
        val newProducts = ArrayList<Product>()
        products.forEach {
            val imageName = File(it.img).name
            DebugLog.e("Image name upload: ${it.img}")
            val product = Product(it.name, it.price, imageName)
            newProducts.add(product)
        }
        productList.clear()
        productList.add(requestBody(Gson().toJson(newProducts).toString()))

        // Get products
        for (i in 0 until products.size) {
            // Check image uploaded then skip it
            val regex = products[i].img.contains("chapp.vn")
            if (regex) continue
            // Add new image for upload
            imgList.add(prepareImageForRequest("img_product[]", File(products[i].img)))
        }

        /////////////////// Image cover //////////////////////
        // Upload cover images JSON Object
        val coverImages = ArrayList<RequestBody>()
        val imagesListObject =  ArrayList<Images>()
        imgSwipeList.forEach {
            val imageName = File(it).name
            DebugLog.e("Image name upload: $imageName")
            val images = Images(postId, imageName)
            imagesListObject.add(images)
        }
        coverImages.clear()
        coverImages.add(requestBody(Gson().toJson(imagesListObject).toString()))
        // Upload cover image file
        for (i in 0 until imgSwipeList.size) {
            // Check image uploaded then skip it
            val regex = imgSwipeList[i].contains("chapp.vn")
            if (regex) continue
            // Add new image for upload
            requestCoverFile.add(prepareImageCoverForRequest("img[]", File(imgSwipeList[i])))
        }
        networkState.postValue(NetworkState.LOADING)
        // Send request data
        repository.editPostProductRequest(params, productList, imgList, coverImages, requestCoverFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    postProductResponse.postValue(result) },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }
}