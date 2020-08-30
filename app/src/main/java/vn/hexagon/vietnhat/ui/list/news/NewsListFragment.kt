package vn.hexagon.vietnhat.ui.list.news

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_news_list.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.EndlessScrollingRecycler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.news.NewsListResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentNewsListBinding
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
 * Create on：2019-09-25
 * =====================================================
 */
class NewsListFragment: MVVMBaseFragment<FragmentNewsListBinding, PostListViewModel>() {

    // View model
    private lateinit var postListViewModel: PostListViewModel
    // Adapter
    private lateinit var newAdapter: NewListAdapter
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
        simpleTitleText = getString(R.string.create_post_news)
        leftButtonVisible = true
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack(R.id.newsListFragment, true)
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_news_list

    override fun initView() {
        // init adapter
        newAdapter = NewListAdapter(postListViewModel, ::onItemClick)
        // Refresh layout
        newListRefresher.setOnRefreshListener {
            loadData(Constant.POST_PER_PAGE)
        }
        activity?.let { context ->
            newRecyclerView.apply {
                setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = newAdapter
                addOnScrollListener(object: EndlessScrollingRecycler(linearLayoutManager) {
                    override fun onLoadMore(numberPost: Int) {
                        DebugLog.e("COCA: $numberPost")
                        loadData(numberPost * 20)
                    }
                })
            }
        }
        // Response data
        postListViewModel.newListResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Network response
        postListViewModel.networkState.observe(this, Observer { status ->
            newProgressBarList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            newErrMsgList.visibility =
                if (postListViewModel.listIsEmpty() && status == NetworkState.ERROR) View.VISIBLE else View.GONE
            if (!postListViewModel.listIsEmpty()) {
                newAdapter.setNetworkState(status)
            }
        })
    }

    /**
     * Fetch data list
     *
     */
    private fun loadData(numberPost:Int) {
        userId?.let {
            postListViewModel.requestNewsList(
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
    private fun getResponse(response: NewsListResponse) {
        newListRefresher.isRefreshing = false
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.e("Fetch List Success: ${response.errorId}")
            newAdapter.submitList(response.data)
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    override fun initAction() {
    }

    /**
     * Go to detail item
     *
     */
    private fun onItemClick(newsId: String) {
        val action = NewsListFragmentDirections.actionNewsListFragmentToNewsDetailFragment(newsId)
        findNavController().navigate(action)
    }
}