package vn.hexagon.vietnhat.ui.setting.guide

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.action_bar_base.view.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.databinding.FragmentGuideDetailBinding

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
class GuideDetailFragment: MVVMBaseFragment<FragmentGuideDetailBinding, GuideViewModel>() {
    // Account Info View Model
    private lateinit var guideViewModel: GuideViewModel
    override fun getBaseViewModel(): GuideViewModel {
        guideViewModel = ViewModelProviders.of(this, viewModelFactory)[GuideViewModel::class.java]
        return guideViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val guideId = arguments?.let { GuideDetailFragmentArgs.fromBundle(it).guideId }
        guideId?.let { guideViewModel.getGuideDetail(it) }
        val dataBinding = getBaseViewDataBinding()
        guideViewModel.guideDetailResponse.observe(this, Observer { response ->
            dataBinding.guide = response.data
        })
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

    override fun getLayoutId(): Int = R.layout.fragment_guide_detail

    override fun initView() {
    }

    override fun initAction() {
    }
}