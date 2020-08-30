package vn.hexagon.vietnhat.ui.setting.notification

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.action_bar_base.view.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.databinding.FragmentNotifyDetailBinding
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-10-24 
 */
class NotifyDetailFragment: MVVMBaseFragment<FragmentNotifyDetailBinding, NotifyViewModel>() {
    // View model
    private lateinit var notifyViewModel: NotifyViewModel
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    var userId: String? = Constant.BLANK

    override fun getBaseViewModel(): NotifyViewModel {
        notifyViewModel = ViewModelProviders.of(this, viewModelFactory)[NotifyViewModel::class.java]
        return notifyViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val dataBinding = getBaseViewDataBinding()
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        userId?.let { notifyViewModel.getNotifyList(it) }
        val notifyId = arguments?.let { NotifyDetailFragmentArgs.fromBundle(it).notifyId }
        notifyViewModel.notifyListResponse.observe(this, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD) {
                response.data.forEach {
                    if (it.id == notifyId) {
                        dataBinding.notify = it
                    }
                }
            }
        })
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.setting_notify_title)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_notify_detail

    override fun initView() {
    }

    override fun initAction() {
    }
}