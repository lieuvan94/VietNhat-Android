package vn.hexagon.vietnhat.ui.auth

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.repository.auth.LoginRepository
import javax.inject.Inject

/**
 * Created by VuNBT on 2019-08-10.
 */
class LoginFragmentViewModel @Inject constructor(private val repository: LoginRepository) : MVVMBaseViewModel() {
    val loginResponse = MutableLiveData<LoginResponse>()

    /**
     * Get login with phone number
     * @param phone Phone number
     * @param password Password
     */
    fun doLogin(phone:String, password:String) {
        repository.executeLogin(phone, password)
            .applyScheduler()
            .subscribe(
                {result -> loginResponse.postValue(result)},
                {throwable -> DebugLog.e(throwable.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get login with Facebook
     * @param phone Phone number
     * @param password Password
     * @param name user name
     */
    fun doFbLogin(email:String, fbid:String, name:String) {
        repository.executeFbLogin(email, fbid, name)
            .applyScheduler()
            .subscribe(
                {result -> loginResponse.postValue(result)},
                {throwable -> DebugLog.e(throwable.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get login with G-plus
     * @param phone Phone number
     * @param password Password
     * @param name user name
     */
    fun doGgLogin(email:String, ggid:String, name:String) {
        repository.executeGgLogin(email, ggid, name)
            .applyScheduler()
            .subscribe(
                {result -> loginResponse.postValue(result)},
                {throwable -> DebugLog.e(throwable.toString())}
            ).addToCompositeDisposable(compositeDisposable)
    }

}