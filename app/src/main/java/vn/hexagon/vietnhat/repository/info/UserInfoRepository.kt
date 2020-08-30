package vn.hexagon.vietnhat.repository.info

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.model.auth.PasswordResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-27 
 */
class UserInfoRepository @Inject constructor(private val apiService: NetworkService) {


    /**
     * Request user data
     *
     * @param userId
     * @return user data
     */
    fun getUserInfo(userId: String): Single<LoginResponse> {
        return apiService.getUserInfo(userId)
    }

    /**
     * Request update profile
     *
     * @param userId
     * @param email
     * @param phone
     * @param name
     * @param address
     * @return Update user response
     */
    fun requestUpdateInfo(userId: String, email:String, phone: String, name:String, address:String): Single<LoginResponse> {
        return apiService.requestUpdateProfile(userId, email, phone, name, address)
    }

    /**
     * Request update password
     *
     * @param phone
     * @param password
     * @return Update password response
     */
    fun requestChangePassword(phone: String, password:String): Single<PasswordResponse> {
        return apiService.requestUpdatePassword(phone, password)
    }

    /**
     * Request upload avatar
     *
     * @param userId
     * @param img
     * @return
     */
    fun requestUploadAvatar(userId: RequestBody, img: MultipartBody.Part?): Single<LoginResponse> {
        return apiService.uploadAvatar(userId, img)
    }

    /**
     * Request verify code
     *
     * @param phone
     * @return
     */
    fun requestVerifyCode(phone: String): Single<VerifyResponse> {
        return apiService.getCodeVerify(phone)
    }

    /**
     * Request forgot password
     *
     * @param phone
     * @return sms code
     */
    fun requestForgotPassword(phone: String): Single<VerifyResponse> {
        return apiService.requestForgotPassword(phone)
    }
}