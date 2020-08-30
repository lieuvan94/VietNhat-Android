package vn.hexagon.vietnhat.ui.favourite


import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_unlogin_common.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentFavouriteBinding
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import javax.inject.Inject


/*
 * Create by VuNBT on 2019-09-26
 */
class FavouriteFragment: MVVMBaseFragment<FragmentFavouriteBinding, FavoriteViewModel>(),
    View.OnClickListener {

    // View model
    private lateinit var favListViewModel: FavoriteViewModel
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    var userId: String? = Constant.BLANK
    // Adapter
    private lateinit var favAdapter: FavouriteListAdapter
    // Init list
    private var favList: ArrayList<Post> = ArrayList()

    override fun getBaseViewModel(): FavoriteViewModel {
        favListViewModel =
            ViewModelProviders.of(this, viewModelFactory)[FavoriteViewModel::class.java]
        return favListViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id),
            Constant.BLANK)
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_fav)
        leftButtonVisible = false
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_favourite

    override fun initView() {
        // Init layout by condition
        if (!isToken()) {
            processBeforeLogin()
        } else {
            processAfterLogin()
        }
    }

    /**
     * Handle events onClick items
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.unLoginRegister -> findNavController().navigate(R.id.registerFragment)
            R.id.unLoginSignIn -> findNavController().navigate(R.id.loginFragment)
        }
    }

    /**
     * Handle layout when user not login
     *
     */
    private fun processBeforeLogin() {
        favUnLoginArea.visibility = View.VISIBLE
        unLoginRegister.setOnClickListener(this)
        unLoginSignIn.setOnClickListener(this)
    }

    /**
     * Handle layout after user login
     *
     */
    private fun processAfterLogin() {
        favUnLoginArea.visibility = View.GONE
        activity?.let { context ->
            // Init adapter
            favAdapter = FavouriteListAdapter(context, favListViewModel, ::onItemClick, ::onDeleteClick)
            // Refresh layout
            favListRefresher.setOnRefreshListener {
                loadData(null)
                favAdapter.notifyDataSetChanged()
                favFilterSpinner.setSelection(0, true)
            }

            // Init drop down list filter
            ArrayAdapter.createFromResource(context,
                R.array.listFilterDisplay,
                R.layout.spinner_item).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                favFilterSpinner.adapter = adapter
            }
            // Handle filter by services
            filterServiceProcess()
            // Handle recycler view
            favRecyclerView.apply {
                setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = favAdapter
            }
            // Response data
            favListViewModel.favouriteResponse.observe(viewLifecycleOwner, Observer { response ->
                if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    getResponse(response)
                }
            })
        }
        // Remove Fav response
        favListViewModel.removeFavouriteResponse.observe(viewLifecycleOwner, Observer { response ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                DebugLog.e(response.message)
                removeFavResponse(response)
            }
        })
        // Network response
        favListViewModel.networkState.observe(this, Observer { status ->
            favProgressBarList.visibility =
                if (status != null && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            favErrMsgList.visibility =
                if (status != null &&  status == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
    }

    /**
     * Handle remove items
     *
     * @param list
     * @param position
     */
    private fun FavouriteListAdapter.processRemoveItem(list: ArrayList<Post>, position: Int) {
        val favListUpdated = list.removeAt(position)
        this.apply {
            onCurrentListChanged(list, mutableListOf(favListUpdated))
            notifyItemRemoved(position)
            notifyItemChanged(position)
        }
    }

    /**
     * Get filter result by services
     *
     */
    private fun filterServiceProcess() {
        favFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val serviceName = resources.getStringArray(R.array.listFilterDisplay)[position]
                val request = WindyConvertUtil.getServiceId(serviceName)
                if (position != 0) loadData(request) else loadData(null)
            }

        }
    }

    /**
     * Response and handle event after add fav
     *
     * @param response addFav
     */
    private fun removeFavResponse(response: ListPostResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e(response.message)
            Snackbar.make(favParentArea, getString(R.string.remove_favourite_message), Snackbar.LENGTH_SHORT).show()
        } else {
            showAlertDialog("Remove Favourite", response.message, "OK")
        }
    }

    /**
     * Fetch favourite list
     *
     */
    private fun loadData(serviceId: String?) {
        userId?.let {
            favListViewModel.getFavList(
                it,
                serviceId)
        }
        favAdapter.notifyDataSetChanged()
    }

    /**
     * Response from server fetching fav list
     *
     * @param response
     */
    private fun getResponse(response: ListPostResponse) {
        favListRefresher.isRefreshing = false
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch List Success: ${response.errorId}")
            favList = response.data
            favAdapter.insertItem(response.data)
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    override fun initAction() {
    }

    /**
     * Get token
     *
     * @return token (true is hasToken)
     */
    private fun isToken(): Boolean {
        val token = sharedPreferences.getString(
            getString(R.string.token),
            Constant.BLANK
        )
        return token == Constant.TOKEN
    }

    /**
     * Handle onClick menu delete button
     *
     * @param position
     */
    private fun onDeleteClick(position: Int) {
        userId?.let { userId ->
            favListViewModel.removeFavouriteRequest(
                userId,
                favAdapter.currentList[position].id
            )
        }
        favAdapter.processRemoveItem(favList, position)
    }

    /**
     * Go to detail item
     *
     * @param userId
     * @param postId
     */
    private fun onItemClick(userId:String, postId:String, serviceId: String?) {
        when(serviceId) {
            Constant.DELIVER_SERVICE_ID -> {
                val action = FavouriteFragmentDirections.actionFavouriteFragmentToDeliverDetailFragment(postId, userId)
                findNavController().navigate(action)
            }
            else -> {
                val action = FavouriteFragmentDirections.actionFavouriteFragmentToPostDetailFragment(userId, postId)
                findNavController().navigate(action)
            }
        }
    }
}
