package vn.hexagon.vietnhat.ui.auth

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.repository.auth.RegisterRepository
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
class RegisterViewModel @Inject constructor(private val repository: RegisterRepository) : MVVMBaseViewModel() {

    // Register response
    private val registerResponse = MutableLiveData<LoginResponse>()
    // Verify code response
    val verifyCodeResponse = MutableLiveData<VerifyResponse>()

    fun getResponse(): MutableLiveData<LoginResponse> {
        return registerResponse
    }

    /**
     * Request register account
     *
     * @param name
     * @param phone
     * @param password
     */
    fun doSignUp(name: String, phone: String, password: String) {
        networkState.postValue(NetworkState.LOADING)
        repository.executeSignUp(name, phone, password)
            .applyScheduler()
            .subscribe(
                { result ->
                    networkState.postValue(NetworkState.LOADED)
                    registerResponse.postValue(result)
                },
                { throwable ->
                    networkState.postValue(NetworkState.ERROR)
                    DebugLog.e(throwable.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

    /**
     * Get verify code
     *
     * @param phone
     */
    fun getVerifyCode(phone: String) {
        repository.getVerifyCode(phone)
            .applyScheduler()
            .subscribe(
                { result ->
                    verifyCodeResponse.postValue(result)
                },
                { throwable ->
                    DebugLog.e(throwable.message.toString())
                }
            ).addToCompositeDisposable(compositeDisposable)
    }

}