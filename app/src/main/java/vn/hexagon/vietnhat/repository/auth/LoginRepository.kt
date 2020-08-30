package vn.hexagon.vietnhat.repository.auth

import io.reactivex.Single
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.remote.NetworkService
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-11 
 */
class LoginRepository @Inject constructor(private val apiService:NetworkService) {
    /**
     * Login with phone number
     */
    fun executeLogin(phone:String, password: String): Single<LoginResponse> {
        return apiService.doLogin(phone, password)
    }

    /**
     * Login with facebook account
     *
     * @param email user email
     * @param fbid facebook id
     * @param name facebook user name
     * @return network service (send facebook info)
     */
    fun executeFbLogin(email:String, fbid:String, name:String): Single<LoginResponse> {
        return apiService.sendFbInfo(email, fbid, name)
    }

    /**
     * Login with g-plus account
     *
     * @param email g-plus email
     * @param ggid g-plus id
     * @param name g-plus name
     * @return network service (g-plus)
     */
    fun executeGgLogin(email:String, ggid:String, name:String): Single<LoginResponse> {
        return apiService.sendGgInfo(email, ggid, name)
    }

}