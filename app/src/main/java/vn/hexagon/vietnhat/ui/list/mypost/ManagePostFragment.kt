package vn.hexagon.vietnhat.ui.list.mypost

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
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.fragment_unlogin_common.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.EndlessScrollingRecycler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.data.model.service.ListPostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentPostBinding
import vn.hexagon.vietnhat.ui.dialog.PostSelectDialogFragment
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
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
 * Create on：2019-10-05
 * =====================================================
 */
class ManagePostFragment: MVVMBaseFragment<FragmentPostBinding, ManagePostViewModel>(),
    View.OnClickListener {

    // View model
    private lateinit var managePostViewModel: ManagePostViewModel
    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    var userId: String? = Constant.BLANK
    // Service ID
    var serviceId: String? = Constant.BLANK
    // Post list
    private lateinit var postList: ArrayList<Post>
    // Post Adapter
    private lateinit var postAdapter: PersonalPostAdapter

    override fun getBaseViewModel(): ManagePostViewModel {
        managePostViewModel = ViewModelProviders
            .of(this, viewModelFactory)[ManagePostViewModel::class.java]
        return managePostViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id),
            Constant.BLANK
        )
        loadData(Constant.POST_PER_PAGE)
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity)

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_post

    override fun initView() {
        // Action bar
        actionBar?.apply {
            simpleTitleText = getString(R.string.post_screen_title)
            leftButtonVisible = false
            rightButtonVisible = isToken()
            rightButtonResource = R.drawable.ic_post
            rightActionBarButton.setOnClickListener {
                fragmentManager?.let {
                    PostSelectDialogFragment().show(it, "post")
                }
            }
        }
        // Init layout by condition
        if (!isToken()) {
            processBeforeLogin()
        } else {
            processAfterLogin()
        }
    }

    /**
     * Handle layout when user not login
     *
     */
    private fun processBeforeLogin() {
        unLoginArea.visibility = View.VISIBLE
        unLoginRegister.setOnClickListener(this)
        unLoginSignIn.setOnClickListener(this)
    }

    private fun processAfterLogin() {
        // Init post list
        postList = ArrayList()
        // Init adapter
        postAdapter = PersonalPostAdapter(managePostViewModel, ::onItemClick, ::onDeleteClick)
        // Refresh layout
        managePostRefreshList.setOnRefreshListener {
            loadData(Constant.POST_PER_PAGE)
        }

        activity?.let { context ->
            // Init drop down list filter
            ArrayAdapter.createFromResource(
                context,
                R.array.listFilterDisplay,
                R.layout.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                postSpinner.adapter = adapter
            }
            // Get filter condition selected
            getItemSelected()
            // Recycler view init
            postRecycler.apply {
                setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = postAdapter
                addOnScrollListener(object: EndlessScrollingRecycler(linearLayoutManager) {
                    override fun onLoadMore(numberPost: Int) {
                        DebugLog.e("COCA: $numberPost")
                        loadData(numberPost * 20)
                    }
                })
            }
        }
        // Response data
        managePostViewModel.personalPostResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Response after delete
        managePostViewModel.personalPostRemoveResponse.observe(viewLifecycleOwner, Observer { response ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                getResponseAfterRemoved(response)
            }
        })
        // Network response
        managePostViewModel.networkState.observe(this, Observer { status ->
            postListProgress.visibility =
                if (managePostViewModel.listIsEmpty() && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            errMsgOnList.visibility =
                if (managePostViewModel.listIsEmpty() && status == NetworkState.ERROR) View.VISIBLE else View.GONE
            if (!managePostViewModel.listIsEmpty()) {
                postAdapter.setNetworkState(status)
            }
        })
    }

    /**
     * Handle remove items
     *
     * @param position
     */
    private fun PersonalPostAdapter.processRemoveItem(list: ArrayList<Post>, position: Int) {
        val manageListUpdate = list.removeAt(position)
        this.apply {
            onCurrentListChanged(list, mutableListOf(manageListUpdate))
            notifyItemRemoved(position)
            notifyItemChanged(position)
        }
        managePostFragmentParent.layoutTransition
    }

    /**
     * Fetch data list
     *
     */
    private fun loadData(numberPost:Int) {
        userId?.let {
            managePostViewModel.getPersonalPostList(
                it,
                serviceId,
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
        managePostRefreshList.isRefreshing = false
        if (response.errorId == Constant.RESPOND_CD) {
            postList = response.data
            postAdapter.submitList(response.data)
            postAdapter.notifyDataSetChanged()
            DebugLog.e("Fetch List Success: ${response.errorId}")
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Get response after remove post
     *
     * @param response
     */
    private fun getResponseAfterRemoved(response: ListPostResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            Snackbar.make(managePostFragmentParent, getString(R.string.remove_alert_message), Snackbar.LENGTH_SHORT).show()
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Handle onClick events
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.unLoginRegister -> {
                findNavController().navigate(R.id.registerFragment)
            }
            R.id.unLoginSignIn -> findNavController().navigate(R.id.loginFragment)
        }
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

    override fun initAction() {
    }

    private fun getItemSelected() {
        // Get id of item last selected
        postSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val listFilter = resources.getStringArray(R.array.listFilterDisplay)
                DebugLog.e("${listFilter[position].toString()} - ${WindyConvertUtil.getServiceId(listFilter[position].toString())}")
                serviceId = if (position != 0) WindyConvertUtil.getServiceId(listFilter[position].toString()) else null
                loadData(Constant.POST_PER_PAGE)
            }
        }
    }

    /**
     * Handle onClick menu delete button
     *
     * @param position
     */
    private fun onDeleteClick(position: Int) {
        // Request remove item
        userId?.let {
            managePostViewModel.requestRemovePost(
                it,
                postAdapter.currentList[position].id
            )
        }
        // Remove item from adapter
        postAdapter.processRemoveItem(postList, position)
    }

    /**
     * Go to detail item
     *
     * @param postId
     */
    private fun onItemClick(userId:String, postId:String, serviceId:String) {
        when(serviceId) {
            Constant.DELIVER_SERVICE_ID -> {
                val action = ManagePostFragmentDirections.actionManagePostFragmentToDeliverDetailFragment(postId, userId)
                findNavController().navigate(action)
            }
            else -> {
                val action = ManagePostFragmentDirections.actionManagePostFragmentToPostDetailFragment(userId, postId)
                findNavController().navigate(action)
            }
        }
    }
}