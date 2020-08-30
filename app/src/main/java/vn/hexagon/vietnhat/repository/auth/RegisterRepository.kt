package vn.hexagon.vietnhat.repository.auth

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-11 
 */
class RegisterRepository @Inject constructor(private val apiService: NetworkService) {

    /**
     * Request register account
     *
     * @param name
     * @param phone
     * @param password
     * @return
     */
    fun executeSignUp(name:String, phone:String, password:String): Single<LoginResponse> {
        return apiService.doSignUp(name, phone, password)
    }

    /**
     * Get code verify
     *
     * @param phone
     * @return
     */
    fun getVerifyCode(phone: String): Single<VerifyResponse> {
        return apiService.getCodeVerify(phone)
    }
}