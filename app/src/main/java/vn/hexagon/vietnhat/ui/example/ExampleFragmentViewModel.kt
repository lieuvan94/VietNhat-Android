package vn.hexagon.vietnhat.ui.example

import androidx.lifecycle.MutableLiveData
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseViewModel
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.addToCompositeDisposable
import vn.hexagon.vietnhat.base.utils.applyScheduler
import vn.hexagon.vietnhat.data.model.ExampleUser
import vn.hexagon.vietnhat.repository.ExampleRepository
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-07-31.
 */
class ExampleFragmentViewModel @Inject constructor(
    private val userRepo: ExampleRepository
) : MVVMBaseViewModel() {
    var userExample: MutableLiveData<ExampleUser>? = MutableLiveData()

    fun getUserFromRemote(id: String) {
        userRepo.getUserFromRemote(id)
            .applyScheduler()
            .subscribe({
                userExample?.postValue(it)
                DebugLog.e("${userExample?.value}")
            }, {
                //TODO: Show error
            })
            .addToCompositeDisposable(compositeDisposable)
    }

    fun getUserFromDB() {
        userRepo.getUserFromDB()
            .applyScheduler()
            .subscribe({
                //TODO: Show list from DB
            }, {
                //TODO: Show error
            })
            .addToCompositeDisposable(compositeDisposable)
    }
}