package vn.hexagon.vietnhat.ui.home

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.banner.BannerResponse
import vn.hexagon.vietnhat.repository.home.BannerRepository
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-08-17.
 */
class HomeViewModel @Inject constructor(private val repository: BannerRepository) :
    MVVMBaseViewModel() {
    // Banner Response
    val bannerResponse = MutableLiveData<BannerResponse>()

    /**
     * Get app banner
     *
     */
    fun getBanner() {
        repository.getHomeBanner()
            .applyScheduler()
            .subscribe(
                { result -> bannerResponse.postValue(result) },
                { throwable -> DebugLog.e(throwable.message.toString()) }
            ).addToCompositeDisposable(compositeDisposable)
    }
}