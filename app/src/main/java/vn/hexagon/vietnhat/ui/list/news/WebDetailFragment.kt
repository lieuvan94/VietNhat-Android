package vn.hexagon.vietnhat.ui.list.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_news_detail.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.news.NewsDetailResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentNewsDetailBinding
import vn.hexagon.vietnhat.ui.list.PostListViewModel

/*
 * Create by VuNBT on 2019-12-19 
 */
class WebDetailFragment: MVVMBaseFragment<FragmentNewsDetailBinding, PostListViewModel>()  {

    // View model
    private lateinit var viewModel: PostListViewModel
    // News ID
    private var mPostId = Constant.BLANK
    // Progress bar
    private lateinit var mProgressBar: ProgressbarHandler

    override fun getBaseViewModel(): PostListViewModel {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[PostListViewModel::class.java]
        return viewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        argument?.let {
            mPostId = WebDetailFragmentArgs.fromBundle(it).postId
        }
        // Request new detail
        viewModel.requestNewsDetail(mPostId)
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_news)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_news_detail


    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        activity?.let { context ->
            mProgressBar = ProgressbarHandler(context)
        }
        // Response data detail
        viewModel.newDetailResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Response network state
        viewModel.networkState.observe(this, Observer { response ->
            handleProgressBar(response)
        })

    }

    /**
     * Handle response progressbar
     *
     * @param response
     */
    private fun handleProgressBar(response: NetworkState) {
        when(response) {
            NetworkState.LOADING -> mProgressBar.show()
            NetworkState.LOADED, NetworkState.ERROR -> mProgressBar.hide()
        }
    }

    /**
     * Handle response of News detail
     *
     * @param response
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun getResponse(response: NewsDetailResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch List Success: ${response.errorId}")
            newDetailWebView.apply {
                settings.javaScriptEnabled = true
                loadData(response.data.content, "text/html", "UTF-8")
            }
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    override fun initAction() {
    }
}