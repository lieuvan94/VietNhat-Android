package vn.hexagon.vietnhat.ui.favourite

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_liked_list.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.databinding.FragmentLikedListBinding
import vn.hexagon.vietnhat.ui.comment.CommentFragmentArgs
import vn.hexagon.vietnhat.ui.detail.PostDetailFragmentDirections
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel
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
 * Create on：2019-12-29
 * =====================================================
 */
class LikedListFragment: MVVMBaseFragment<FragmentLikedListBinding, PostDetailViewModel>() {

    // View model
    private lateinit var likedViewModel: PostDetailViewModel
    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }
    // Liked list adapter
    private lateinit var likedListAdapter: LikedListAdapter
    // Shared preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    private var userId:String? = Constant.BLANK
    // Post ID
    private var postId = Constant.BLANK

    override fun getBaseViewModel(): PostDetailViewModel {
        likedViewModel = ViewModelProviders.of(this, viewModelFactory)[PostDetailViewModel::class.java]
        return likedViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        argument?.let {
            postId = LikedListFragmentArgs.fromBundle(it).postId
            userId?.let { userId -> likedViewModel.getDetailPost(userId, postId) }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity)

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_liked_list

    override fun initView() {
        actionBar?.apply {
            simpleTitleText = getString(R.string.liked_list_title)
            leftButtonVisible = true
            leftActionBarButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        // Refresh layout trigger
        likedRefreshLayout.setOnRefreshListener {
            userId?.let { likedViewModel.getDetailPost(it, postId) }
        }
        // Liked list adapter
        likedListAdapter = LikedListAdapter(likedViewModel, ::onMsg, ::onCalling)
        // Init recyclerView
        activity?.let { context ->
            likedRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = likedListAdapter
            }
        }
        // Response post detail
        likedViewModel.detailPostResponse.observe(viewLifecycleOwner, Observer { response ->
            handleDetailResponse(response)
        })
    }

    /**
     * Handle detail post response
     *
     * @param response
     */
    private fun handleDetailResponse(response: PostDetailResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            likedListAdapter.submitList(response.data.likes)
            likedRefreshLayout.isRefreshing = false
        } else {
            showAlertDialog("Error", response?.message, "OK")
        }
    }

    /**
     * Handle when user tap on call button
     *
     * @param phone
     */
    private fun onCalling(phone: String) {
        if (phone == Constant.BLANK) {
            Toast.makeText(context, R.string.error_phone_number, Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        activity?.let {context ->
            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    /**
     * Handle when user tap on msg button
     *
     * @param userId
     * @param userName
     * @param userAvt
     * @param userPhone
     */
    private fun onMsg(userId: String, userName: String, userAvt: String, userPhone: String) {
        if (isToken()) {
            val action = LikedListFragmentDirections
                .actionLikedListFragmentToChatDetailFragment(userId, userName, userAvt, userPhone)
            findNavController().navigate(action)
        } else {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun initAction() {
    }

    /**
     * Get token
     *
     * @return token (true is has token)
     */
    private fun isToken(): Boolean {
        val token = sharedPreferences.getString(
            getString(R.string.token),
            Constant.BLANK
        )
        return token == Constant.TOKEN
    }
}