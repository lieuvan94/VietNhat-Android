package vn.hexagon.vietnhat.ui.setting.account

import androidx.lifecycle.MutableLiveData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.model.auth.PasswordResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.repository.info.UserInfoRepository
import java.io.File
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
 * Create on：2019-09-10
 * =====================================================
 */
class AccountInfoViewModel @Inject constructor(private val repository: UserInfoRepository): MVVMBaseViewModel() {
    // User info response
    val userInfoResponse = MutableLiveData<LoginResponse>()
    // Change password response
    val changePasswordResponse = MutableLiveData<PasswordResponse>()
    // Update profile response
    val updateProfileResponse = MutableLiveData<LoginResponse>()
    // Upload avatar response
    val uploadAvatarResponse = MutableLiveData<LoginResponse>()
    // User info old password
    var userOldPassword:String = Constant.BLANK
    // Verify code response
    val verifyCodeResponse = MutableLiveData<VerifyResponse>()
    // User fields
    var userName:String = Constant.BLANK
    var userEmail:String = Constant.BLANK
    var userPassword:String = Constant.BLANK
    var userAddress:String? = Constant.BLANK
    var userPhone:String = Constant.BLANK
    var img:String? = Constant.BLANK

    /**
     * Get user info
     *
     * @param userId
     */
    fun getUserInfo(userId: String) {
        repository.getUserInfo(userId)
            .applyScheduler()
            .subscribe(
                { result -> userInfoResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request update profile
     *
     * @param userId
     * @param email
     * @param phone
     * @param name
     * @param address
     */
    fun sendRequestUpdateInfo(userId: String, email:String, phone: String, name:String, address:String) {
        repository.requestUpdateInfo(userId, email, phone, name, address)
            .applyScheduler()
            .subscribe(
                { result -> updateProfileResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request update password
     *
     * @param phone
     * @param password
     */
    fun sendRequestChangePassword(phone: String, password:String) {
        repository.requestChangePassword(phone, password)
            .applyScheduler()
            .subscribe(
                { result -> changePasswordResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request upload avatar
     *
     * @param userId
     */
    fun requestUploadAvt(userId:String) {
        // Request userId
        val requestUserId = requestBody(userId)
        // Get image file
        var requestFile : MultipartBody.Part? = null
        if (img != null && !img.equals(Constant.BLANK)) {
            val file = File(img)
            // Upload image file

            val requestImg = RequestBody.create(MediaType.parse("*/*"), file)
            requestFile = MultipartBody.Part.createFormData("img", file.name, requestImg)
        }
        // Request data
        repository.requestUploadAvatar(requestUserId, requestFile)
            .applyScheduler()
            .subscribe(
                { result ->
                    uploadAvatarResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request verify code
     *
     */
    fun requestVerifyCode() {
        repository.requestVerifyCode(userPhone)
            .applyScheduler()
            .subscribe(
                { result -> verifyCodeResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Request forgot password
     *
     */
    fun requestForgotPassword() {
        repository.requestForgotPassword(userPhone)
            .applyScheduler()
            .subscribe(
                { result -> verifyCodeResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }

}