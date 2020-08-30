package vn.hexagon.vietnhat.ui.splash

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.databinding.FragmentSplashBinding
import vn.hexagon.vietnhat.ui.home.HomeViewModel

/*
 * Create by VuNBT on 2019-11-01 
 */
class SplashFragment: MVVMBaseFragment<FragmentSplashBinding, HomeViewModel>() {
    companion object {
        private const val TIME_SPLASH_INTERVAL = 3000L
    }
    override fun getBaseViewModel(): HomeViewModel {
        return ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
    }

    override fun isShowActionBar(): View? = null

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_splash

    override fun initView() {
        // Read splash logo from svg
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        // Interval then trans to home screen
        Handler().postDelayed({
            findNavController().navigate(
                R.id.action_splashFragment_to_homeFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
            )
        }, TIME_SPLASH_INTERVAL)
    }

    override fun initAction() {
    }
}