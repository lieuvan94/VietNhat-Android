package vn.hexagon.vietnhat.ui.setting.guide

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_guide_list.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentGuideListBinding

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
 * Create on：2019-09-29
 * =====================================================
 */
class GuideListFragment: MVVMBaseFragment<FragmentGuideListBinding, GuideViewModel>() {
    // Account Info View Model
    private lateinit var guideViewModel: GuideViewModel
    override fun getBaseViewModel(): GuideViewModel {
        guideViewModel = ViewModelProviders.of(this, viewModelFactory)[GuideViewModel::class.java]
        return guideViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        // Fetch init data
        guideViewModel.getGuideList()
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.setting_guide_title)
        leftButtonVisible = true
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_guide_list

    override fun initView() {
        // Init adapter
        val guideAdapter = GuideListAdapter(this@GuideListFragment::onClick)
        // Get guide list response
        guideViewModel.guideResponse.observe(this, Observer { response ->
            guideAdapter.submitList(response.data)
        })
        // Get network response
        guideViewModel.networkState.observe(this, Observer { status ->
            guideListProgressArea.visibility =
                if (status != null && (status == NetworkState.LOADING || status == NetworkState.ERROR)) View.VISIBLE else View.GONE
            guideListProgress.visibility =
                if (status != null && status == NetworkState.LOADING) View.VISIBLE else View.GONE
            guideListErrMsg.visibility =
                if (status != null && status == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
        activity?.let { context ->
            guideListRecycler.apply {
                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = linearLayoutManager
                adapter = guideAdapter
            }
        }
    }

    override fun initAction() {
    }

    /**
     * Pass guideId for detail screen
     *
     * @param guideId
     */
    private fun onClick(guideId:String) {
        val action = GuideListFragmentDirections
            .actionGuideListFragmentToGuideDetailFragment(guideId)
        findNavController().navigate(action)
    }
}