package vn.hexagon.vietnhat.ui.list.mart

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
import kotlinx.android.synthetic.main.fragment_mart_list.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.EndlessScrollingRecycler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentMartListBinding
import vn.hexagon.vietnhat.ui.dialog.search.CommonSearchDialogFragment
import vn.hexagon.vietnhat.ui.dialog.search.DialogSearchClickListener
import vn.hexagon.vietnhat.ui.list.PostListViewModel
import javax.inject.Inject

/**
 *
//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//                    Pray for no Bugs
 * =====================================================
 * Name：VuNBT
 * Create on：2019-08-30
 * =====================================================
 */
class MartListFragment : MVVMBaseFragment<FragmentMartListBinding, PostListViewModel>(), DialogSearchClickListener {

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
        userId = sharedPreferences.getString(
            getString(R.string.variable_local_user_id),
            Constant.BLANK
        )
        postListViewModel.userId = userId.toString()
        loadData(Constant.POST_PER_PAGE)
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_mart)
        leftButtonVisible = true
        rightButtonVisible = true
        rightButtonResource = R.drawable.ic_search
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack(R.id.martListFragment, true)
        }
        rightActionBarButton.setOnClickListener {
            fragmentManager?.let {
                val dialog = CommonSearchDialogFragment(Constant.MART_SERVICE_NM)
                dialog.setTargetFragment(this@MartListFragment, 1)
                dialog.show(it, "MartSearchDialog")
            }
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_mart_list

    override fun initView() {
        // init adapter
        val martAdapter = MartListAdapter(postListViewModel,::onItemClick, ::onClickFav)
        // Refresh layout
        martListRefresher.setOnRefreshListener {
            loadData(Constant.POST_PER_PAGE)
        }
        activity?.let { context ->
            martRecyclerView.apply {
                setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = martAdapter
                addOnScrollListener(object : EndlessScrollingRecycler(linearLayoutManager) {
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
            martAdapter.submitList(response.data)
            getResponse(response, "List response")
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
            martProgressBarList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            martErrMsgList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.ERROR) View.VISIBLE else View.GONE
            if (!postListViewModel.listIsEmpty()) {
                martAdapter.setNetworkState(status)
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
    private fun loadData(numberPost: Int) {
        userId?.let {
            postListViewModel.getDataListPost(
                it,
                Constant.MART_SERVICE_ID,
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
    private fun getResponse(response: ListPostResponse, content: String) {
        martListRefresher.isRefreshing = false
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch List Success: ${response.errorId}")
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    override fun initAction() {
    }

    /**
     * Request search by conditions
     *
     * @param title
     * @param address
     */
    override fun searchTriggered(
        title: String?,
        address: String?,
        subjectId: String?
    ) {
        userId?.let {
            postListViewModel.getSearchResultCommon(
                it,
                Constant.MART_SERVICE_ID,
                title,
                address,
                Constant.INDEX,
                Constant.POST_PER_PAGE
            )
        }
    }

    /**
     * Go to detail item
     *
     * @param userId
     * @param postId
     */
    private fun onItemClick(userId:String, postId:String) {
        val action = MartListFragmentDirections.actionMartListFragmentToPostDetailFragment(userId, postId)
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
        Snackbar.make(martListParent, message, Snackbar.LENGTH_SHORT).show()
    }
}