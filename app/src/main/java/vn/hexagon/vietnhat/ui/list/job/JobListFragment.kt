package vn.hexagon.vietnhat.ui.list.job

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_job_list.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.EndlessScrollingRecycler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.BLANK
import vn.hexagon.vietnhat.constant.Constant.JOB_SERVICE_ID
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentJobListBinding
import vn.hexagon.vietnhat.ui.dialog.search.CommonSearchDialogFragment
import vn.hexagon.vietnhat.ui.dialog.search.DialogSearchClickListener
import vn.hexagon.vietnhat.ui.list.PostListViewModel
import javax.inject.Inject

/**
 * Created by VuNBT on 9/23/2019.
 */
class JobListFragment: MVVMBaseFragment<FragmentJobListBinding, PostListViewModel>(), DialogSearchClickListener {

    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }
    // View model
    private lateinit var postListViewModel: PostListViewModel
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    var userId: String? = Constant.BLANK

    override fun getBaseViewModel(): PostListViewModel {
        postListViewModel =
            ViewModelProviders.of(this, viewModelFactory)[PostListViewModel::class.java]
        return postListViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), BLANK)
        postListViewModel.userId = userId.toString()
        loadData(20)
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity)

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_job_list

    override fun initView() {
        actionBar?.apply {
            simpleTitleText = getString(R.string.create_post_job)
            leftButtonVisible = true
            rightButtonVisible = true
            rightButtonResource = R.drawable.ic_search
            leftActionBarButton.setOnClickListener {
                findNavController().popBackStack()
            }
            rightActionBarButton.setOnClickListener {
                fragmentManager?.let {
                    val dialog = CommonSearchDialogFragment(Constant.JOB_SERVICE_NM)
                    dialog.setTargetFragment(this@JobListFragment, 0)
                    dialog.show(it, "SearchDialog")
                }
            }
        }
        // init adapter
        val jobAdapter = JobListAdapter(postListViewModel, this@JobListFragment::onItemClick, this@JobListFragment::onClickFav)
        // Refresh layout
        jobListRefresher.setOnRefreshListener {
            loadData(20)
        }
        activity?.let { context ->
            jobRecyclerView.apply {
                setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = jobAdapter
                addOnScrollListener(object: EndlessScrollingRecycler(linearLayoutManager) {
                    override fun onLoadMore(numberPost: Int) {
                        DebugLog.e("COCA: $numberPost")
                        loadData(numberPost * 20)
                    }
                })
            }
        }
        // Response data
        postListViewModel.postListResponse.observe(this, Observer { response ->
            DebugLog.e("SIZE: ${response.data.size}")
            jobAdapter.submitList(response.data)
            getResponse(response)
        })
        // Add Fav response
        postListViewModel.favouriteResponse.observe(this, Observer { response ->
            DebugLog.e("Add favourite success: " + response.message)
            addFavResponse(response)
        })
        // Remove Fav response
        postListViewModel.unFavouriteResponse.observe(this, Observer { response ->
            DebugLog.e(response.message)
            removeFavResponse(response)
        })
        // Network response
        postListViewModel.networkState.observe(this, Observer { status ->
            jobProgressBarList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            jobErrMsgList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.ERROR) View.VISIBLE else View.GONE
            if (!postListViewModel.listIsEmpty()) {
                jobAdapter.setNetworkState(status)
            }
        })
    }

    /**
     * Response and handle event after add fav
     *
     * @param response addFav
     */
    private fun removeFavResponse(response: ListPostResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e(response.message)
        } else {
            showAlertDialog("Remove Favourite", response.message, "OK")
        }
    }

    /**
     * Response and handle event after remove fav
     *
     * @param response removeFav
     */
    private fun addFavResponse(response: ListPostResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e(response.message)
        } else {
            showAlertDialog("Add Favourite", response.message, "OK")
        }
    }

    /**
     * Fetch data list
     *
     */
    private fun loadData(numberPost:Int) {
        userId?.let {
            postListViewModel.getDataListPost(
                it,
                JOB_SERVICE_ID,
                0,
                numberPost
            )
        }
    }

    /**
     * Response from server after commit post
     *
     * @param response
     */
    private fun getResponse(response: ListPostResponse) {
        jobListRefresher.isRefreshing = false
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch List Success: ${response.errorId}")
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Go to detail item
     *
     * @param userId
     * @param postId
     */
    private fun onItemClick(userId: String, postId: String) {
        val action =
            JobListFragmentDirections.actionJobListFragmentToPostDetailFragment(userId, postId)
        findNavController().navigate(action)
    }

    /**
     * Display snack bar when tap fav button
     *
     * @param isAdded
     */
    private fun onClickFav(isAdded: Boolean) {
        val message = if (isAdded) {
            getString(R.string.add_favourite_message)
        } else {
            getString(R.string.remove_favourite_message)
        }
        Snackbar.make(jobListArea, message, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Request job search result
     *
     * @param title
     * @param address
     */
    override fun searchTriggered(title: String?, address: String?, subjectId: String?) {
        userId?.let {
            postListViewModel.getSearchResultJob(
                it,
                JOB_SERVICE_ID,
                title,
                Constant.INDEX,
                Constant.POST_PER_PAGE,
                address
            )
        }
    }

    override fun initAction() {
    }
}

