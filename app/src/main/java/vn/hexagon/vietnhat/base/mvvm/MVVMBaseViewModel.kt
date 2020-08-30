package vn.hexagon.vietnhat.base.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.remote.NetworkState
import java.io.File

/**
 * Created by NhamVD on 2019-05-21.
 */
abstract class MVVMBaseViewModel : ViewModel() {

  protected val compositeDisposable = CompositeDisposable()
  // Network state
  val networkState = MutableLiveData<NetworkState>()

  override fun onCleared() {
    compositeDisposable.clear()
    compositeDisposable.dispose()
    super.onCleared()
  }

  /**
   * Request body part
   *
   * @param fieldData
   * @return data
   */
  fun requestBody(fieldData :String): RequestBody {
    return RequestBody.create(MediaType.parse("multipart/form-data"), fieldData)
  }

  fun requestBodyJson(fieldData :String): RequestBody {
    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONObject(fieldData).toString())
  }

  /**
   * Request image to server for cover
   *
   * @param img
   * @param key
   * @return MultipartBody.Part
   */
  fun requestImage(img: String, key: String): MultipartBody.Part? {
    var requestFile: MultipartBody.Part? = null

    if (img != Constant.BLANK) {
      val coverFile = File(img)
      val requestCoverImg = RequestBody.create(MediaType.parse("*/*"), coverFile)
      requestFile = MultipartBody.Part.createFormData(key, coverFile.name, requestCoverImg)
    }
    return requestFile
  }

  /**
   * Request image to server for product
   *
   * @param parameterName
   * @param file
   * @return MultipartBody.Part
   */
  fun prepareImageForRequest(parameterName: String, file: File): MultipartBody.Part {
    val imageBody = RequestBody.create(MediaType.parse("*/*"), file)
    return MultipartBody.Part.createFormData(parameterName, file.name, imageBody)
  }

  /**
   * Request image to server for cover
   *
   * @param parameterName
   * @param file
   * @return MultipartBody.Part
   */
  fun prepareImageCoverForRequest(parameterName: String, file: File): MultipartBody.Part {
    val imageBody = RequestBody.create(MediaType.parse("image/*"), file)
    return MultipartBody.Part.createFormData(parameterName, file.name, imageBody)
  }

}