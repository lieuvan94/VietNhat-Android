package vn.hexagon.vietnhat.ui.setting.rate

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.rate.RateResponse
import vn.hexagon.vietnhat.repository.rate.RateRepository
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class RateViewModel @Inject constructor(private val repository: RateRepository): MVVMBaseViewModel() {
    // Rating response
    val rateResponse = MutableLiveData<RateResponse>()
    // Content
    var content:String? = Constant.BLANK

    /**
     * Request rating app
     *
     * @param userId
     * @param star
     */
    fun requestRatingApp(userId: String, star: String) {
        DebugLog.e("$userId - $star - $content")
        repository.requestRate(userId, star, content)
            .applyScheduler()
            .subscribe(
                { result -> rateResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }
}