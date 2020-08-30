package vn.hexagon.vietnhat.ui.zoom

import android.media.Image
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_zoom.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.databinding.FragmentZoomBinding
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel
import kotlin.collections.ArrayList

/*
 * Create by VuNBT on 2019-12-25 
 */
class ZoomFragment: MVVMBaseFragment<FragmentZoomBinding, PostDetailViewModel>() {
    // View model
    private lateinit var viewModel: PostDetailViewModel
    // Array Image
    private var mListImage =  ArrayList<String>()
    // Position image pager selected by user
    private var mPosition = -1
    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }

    override fun getBaseViewModel(): PostDetailViewModel {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[PostDetailViewModel::class.java]
        return viewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {

    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity)

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_zoom

    override fun initView() {
        actionBar?.apply {
            leftButtonVisible = true
            leftActionBarButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        arguments?.let {
            mListImage.addAll(ZoomFragmentArgs.fromBundle(it).uri)
            mPosition = ZoomFragmentArgs.fromBundle(it).pos
        }
        activity?.let { context ->
            DebugLog.e("${mListImage.size}")
            val imgAdapter = ImagePagerAdapter(context, mListImage)
            zoomPager.adapter = imgAdapter
            zoomPager.currentItem = mPosition
            zoomPager.offscreenPageLimit = 0
        }
    }

    override fun initAction() {
    }

}