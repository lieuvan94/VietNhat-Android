package vn.hexagon.vietnhat.ui.comment

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_comment.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.comment.CommentResponse
import vn.hexagon.vietnhat.databinding.FragmentCommentBinding
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel
import javax.inject.Inject


/*
 * Create by VuNBT on 2019-12-23 
 */
class CommentFragment: MVVMBaseFragment<FragmentCommentBinding, PostDetailViewModel>(), View.OnClickListener {
    // View model
    private lateinit var commentViewModel: PostDetailViewModel
    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }
    // Comment adapter
    private lateinit var commentListAdapter: CommentListAdapter
    // UserId
    private var userId:String? = Constant.BLANK
    // Post ID
    private var postId = Constant.BLANK
    // Shared preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun getBaseViewModel(): PostDetailViewModel {
        commentViewModel = ViewModelProviders.of(this, viewModelFactory)[PostDetailViewModel::class.java]
        return commentViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        argument?.let {
            postId = CommentFragmentArgs.fromBundle(it).postId
            userId?.let { userId -> commentViewModel.getDetailPost(userId, postId) }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        leftButtonVisible = true
        leftActionBarButton.setOnLongClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_comment

    override fun initView() {
        actionBar?.apply {
            simpleTitleText = getString(R.string.comment_title)
            leftButtonVisible = true
            leftActionBarButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        // Init adapter
        commentListAdapter = CommentListAdapter(commentViewModel)
        // Init recyclerView
        activity?.let { context ->
            commentRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = commentListAdapter
            }
        }
        // Refresh comment list
        commentRefreshLayout.setOnRefreshListener {
            userId?.let { commentViewModel.getDetailPost(it, postId) }
        }

        // Response from detail post
        commentViewModel.detailPostResponse.observe(viewLifecycleOwner, Observer { response ->
            handleDetailResponse(response)
        })

        // Response from comment post
        commentViewModel.commentResponse.observe(viewLifecycleOwner, Observer { response ->
            handleCommentResponse(response)
        })

        // Handle events for send button
        cmtSendBtn.setOnClickListener(this)
    }

    /**
     * Handle detail post response
     *
     * @param response
     */
    private fun handleDetailResponse(response: PostDetailResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            commentListAdapter.insertList(response.data.comments)
            commentRefreshLayout.isRefreshing = false
        } else {
            showAlertDialog("Error", response?.message, "OK")
        }
    }

    /**
     * Handle comment post response
     *
     * @param response
     */
    private fun handleCommentResponse(response: CommentResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            // Refresh comment list
            userId?.let { commentViewModel.getDetailPost(it, postId) }
            // Clear text/focus at text box
            cmtContentEd.apply {
                setText(Constant.BLANK)
                clearFocus()
            }
            // Close soft keyboard
            val imm: InputMethodManager =
                activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(cmtContentEd.windowToken, 0)
        } else {
            showAlertDialog("Error", response?.message, "OK")
        }
    }

    override fun initAction() {
    }

    /**
     * Request send comment
     *
     */
    private fun requestComment() {
        if (isToken()) {
            if (cmtContentEd.text.isNotEmpty()) {
                userId?.let { commentViewModel.requestComment(it, postId, cmtContentEd.text.toString()) }
            }
        } else {
            findNavController().navigate(R.id.loginFragment)
        }

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cmtSendBtn -> requestComment()
        }
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