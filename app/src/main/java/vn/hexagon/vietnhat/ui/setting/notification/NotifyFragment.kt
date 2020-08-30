package vn.hexagon.vietnhat.ui.setting.notification

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_notify.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.databinding.FragmentNotifyBinding
import vn.hexagon.vietnhat.databinding.FragmentRatingBinding
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-30 
 */
class NotifyFragment: MVVMBaseFragment<FragmentNotifyBinding, NotifyViewModel>() {

    // View model
    private lateinit var notifyViewModel: NotifyViewModel
    // Data binding
    lateinit var dataBinding: FragmentRatingBinding
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun getBaseViewModel(): NotifyViewModel {
        notifyViewModel = ViewModelProviders.of(this, viewModelFactory)[NotifyViewModel::class.java]
        return notifyViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        userId?.let { notifyViewModel.getNotifyList(it) }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.setting_notify_title)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            // Clear current fragment from stack
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_notify

    override fun initView() {
        // Init adapter
        val notifyAdapter = NotifyListAdapter(notifyViewModel, this@NotifyFragment::onItemClick)
        notifyViewModel.notifyListResponse.observe(this, Observer { response ->
            notifyAdapter.submitList(response.data)
        })
        // Init notify list
        activity?.let {context ->
            notifyRecycler.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = notifyAdapter
            }
        }
    }

    /**
     * Handle onClick item notification
     *
     * @param notifyId
     */
    private fun onItemClick(notifyId: String) {
        val action = NotifyFragmentDirections.actionNotifyFragmentToNotifyDetailFragment(notifyId)
        findNavController().navigate(action)
    }

    override fun initAction() {
    }
}