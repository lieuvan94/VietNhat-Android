package vn.hexagon.vietnhat.ui.list.restaurant

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.EndlessScrollingRecycler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentRestaurantListBinding
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
 * Create on：2019-09-26
 * =====================================================
 */
class RestaurantListFragment: MVVMBaseFragment<FragmentRestaurantListBinding, PostListViewModel>(), DialogSearchClickListener {
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
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id),
            Constant.BLANK
        )
        postListViewModel.userId = userId.toString()
        loadData(Constant.POST_PER_PAGE)
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_restaurant)
        leftButtonVisible = true
        rightButtonVisible = true
        rightButtonResource = R.drawable.ic_search
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
        rightActionBarButton.setOnClickListener {
            fragmentManager?.let {
                val dialog = CommonSearchDialogFragment(Constant.RESTAURANT_SERVICE_NM)
                dialog.setTargetFragment(this@RestaurantListFragment, 0)
                dialog.show(it, "SearchDialog")
            }
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_restaurant_list

    override fun initView() {
        // init adapter
        val restaurantAdapter = RestaurantListAdapter(postListViewModel,::onItemClick, ::onClickFav)
        // Refresh layout
        restaurantListRefresher.setOnRefreshListener {
            loadData(Constant.POST_PER_PAGE)
        }
        activity?.let { context ->
            restaurantRecyclerView.apply {
                setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = restaurantAdapter
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
            restaurantAdapter.submitList(response.data)
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
            restaurantProgressBarList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            restaurantErrMsgList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.ERROR) View.VISIBLE else View.GONE
            if (!postListViewModel.listIsEmpty()) {
                restaurantAdapter.setNetworkState(status)
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
                Constant.RESTAURANT_SERVICE_ID,
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
        restaurantListRefresher.isRefreshing = false
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch List Success: ${response.errorId}")
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    override fun initAction() {
    }

    /**
     * Request search result by condition
     *
     * @param title
     * @param address
     * @param subjectId
     */
    override fun searchTriggered(
        title: String?,
        address: String?,
        subjectId: String?
    ) {
        userId?.let {
            postListViewModel.getSearchResultCommon(
                it,
                Constant.RESTAURANT_SERVICE_ID,
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
        val action = RestaurantListFragmentDirections.actionRestaurantListFragmentToPostDetailFragment(userId, postId)
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
        Snackbar.make(restaurantListParent, message, Snackbar.LENGTH_SHORT).show()
    }
}