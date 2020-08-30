package vn.hexagon.vietnhat

import android.app.Activity
import android.app.Application
import androidx.databinding.DataBindingUtil
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import vn.hexagon.vietnhat.binding.FragmentDataBindingComponent
import vn.hexagon.vietnhat.di.AppInjector
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-07-28.
 */
class BaseApp : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        DataBindingUtil.setDefaultComponent(FragmentDataBindingComponent(this))
    }

    override fun activityInjector() = dispatchingAndroidInjector
}