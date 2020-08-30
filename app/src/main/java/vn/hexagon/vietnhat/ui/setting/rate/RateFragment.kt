package vn.hexagon.vietnhat.ui.setting.rate

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_rating.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.databinding.FragmentRatingBinding
import vn.hexagon.vietnhat.ui.dialog.CommonDialogSingleFragment
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class RateFragment: MVVMBaseFragment<FragmentRatingBinding, RateViewModel>() {

    // View model
    private lateinit var rateViewModel: RateViewModel
    // Data binding
    lateinit var dataBinding: FragmentRatingBinding
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun getBaseViewModel(): RateViewModel {
        rateViewModel = ViewModelProviders.of(this, viewModelFactory)[RateViewModel::class.java]
        return rateViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        // Data binding
        dataBinding = getBaseViewDataBinding()
        dataBinding.userId = userId
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.setting_rate_title)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            // Clear current fragment from stack
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_rating

    override fun initView() {
        rateViewModel.rateResponse.observe(this, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD) {
                fragmentManager?.let {
                    val dialog = CommonDialogSingleFragment()
                    dialog.show(it, "CreateCommonDialog")
                }
                findNavController().popBackStack()
            } else {
                showAlertDialog("Error", response?.message, "OK")
            }
        })
        /*var currentRate = ""
        ratingBar.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_MOVE -> {
                        DebugLog.e(ratingBar.rating.toString())
                        currentRate = ratingBar.rating.toString()
                    }
                }
                return false
            }
        })*/
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
//                ratingBar?.rating = currentRate.toFloat()
                dataBinding.star = rating.toString()
            }
    }

    override fun initAction() {
    }
}