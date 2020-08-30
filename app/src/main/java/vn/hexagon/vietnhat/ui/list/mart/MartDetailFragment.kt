package vn.hexagon.vietnhat.ui.list.mart

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.databinding.FragmentMartDetailBinding
import vn.hexagon.vietnhat.repository.mart.MartDetailViewModel
//import vn.hexagon.vietnhat.ui.mart.MartDetailFragmentArgs


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
 * Create on：2019-09-01
 * =====================================================
 */
class MartDetailFragment : MVVMBaseFragment<FragmentMartDetailBinding, MartDetailViewModel>() {
  private lateinit var martDetailViewModel: MartDetailViewModel

  override fun getBaseViewModel(): MartDetailViewModel {
    martDetailViewModel =
      ViewModelProviders.of(this, viewModelFactory).get(MartDetailViewModel::class.java)
    return martDetailViewModel
  }

  override fun getBindingVariable(): Int = BR.viewmodel

  override fun isShowBottomNavigation(): Boolean = false

  override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
    simpleTitleText = getString(R.string.app_name)
    leftButtonVisible = true
    rightButtonVisible = true
    rightButtonResource = R.drawable.ic_search
  }

  override fun isActionBarOverlap(): Boolean = false

  override fun getLayoutId(): Int = R.layout.fragment_mart_detail

  override fun initView() {
    // Init action bar
    activity?.apply {
      mainBottomNavigation.visibility = View.GONE
    }
  }
  override fun initData(argument: Bundle?) {
//    val martId = arguments?.let { MartDetailFragmentArgs.fromBundle(it).martId }
//    val dataBinding = getBaseViewDataBinding()
//    dataBinding.martData = martId?.let { martDetailViewModel.getMartDetail(it) }
  }

  override fun initAction() {}
}