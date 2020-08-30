package vn.hexagon.vietnhat.ui.list.deliver

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.action_bar_base.*
import kotlinx.android.synthetic.main.fragment_detail_deliver.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.detail.Images
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.databinding.FragmentDetailDeliverBinding
import vn.hexagon.vietnhat.ui.detail.PagerDetailAdapter
import vn.hexagon.vietnhat.ui.detail.PostDetailFragmentDirections
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-10-10 
 */
class DeliverDetailFragment: MVVMBaseFragment<FragmentDetailDeliverBinding, PostDetailViewModel>(),
    View.OnClickListener {
    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }
    // Account Info View Model
    private lateinit var postDetailViewModel: PostDetailViewModel
    // Author name
    private var authorName = Constant.BLANK
    // Author phone number
    private var authorPhone = Constant.BLANK
    // Shared preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // ServiceId
    private var serviceId = Constant.BLANK
    // UserId
    private var userId:String? = Constant.BLANK
    // Author ID
    private var authorId:String? = Constant.BLANK
    // Post ID
    private var postId:String = Constant.BLANK
    // Favourite click flag
    private var mIsClickFav = false
    // Post data
    private lateinit var postData: Post
    // Places
    private var mPlaces: PlacesUtils? = null


    override fun getBaseViewModel(): PostDetailViewModel {
        postDetailViewModel = ViewModelProviders.of(this, viewModelFactory)[PostDetailViewModel::class.java]
        return postDetailViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val dataBinding = getBaseViewDataBinding()
        dataBinding.viewmodel = postDetailViewModel
        // Received postId and userId(author)
        argument?.let {
            postId = DeliverDetailFragmentArgs.fromBundle(it).postId
            // Request author info
            userId?.let { userId -> postDetailViewModel.getDetailPost(userId, postId) }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity)

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_detail_deliver

    override fun initView() {
        // Current userId
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        authorId = arguments?.let { DeliverDetailFragmentArgs.fromBundle(it).userId }
        // Init action bar
        actionBar?.apply {
            simpleTitleText = getString(R.string.create_post_deliver)
            leftButtonVisible = true
            rightButtonVisible = userId == authorId
            rightButtonResource = R.drawable.ic_edit
            leftActionBarButton.setOnClickListener {
                findNavController().popBackStack()
            }
            rightActionBarButton.setOnClickListener {
                findNavController().navigate(DeliverDetailFragmentDirections.actionDeliverDetailFragmentToDeliverPostFragment(postId, Constant.MODE_EDIT))
            }
        }
        // Detail post response
        postDetailViewModel.detailPostResponse.observe(viewLifecycleOwner, Observer { response ->
            activity?.let { context ->
                // Places picker init
                mPlaces = PlacesUtils(context)
                val pagerAdapter = PagerDetailAdapter(context, ::onClickImg)
                getResponse(response.data)
                if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    DebugLog.e("SIZE: " + response?.data?.imagesList?.size.toString())
                    // Image cover
                    pagerAdapter.initPager(response.data.imagesList)
                }
            }
        })
        // Add favourite response
        postDetailViewModel.favouriteResponse.observe(this, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD && mIsClickFav) {
                Snackbar.make(deliverDetailParent, getString(R.string.add_favourite_message), Snackbar.LENGTH_SHORT).show()
                mIsClickFav = false
            }
        })
        // Remove favourite response
        postDetailViewModel.unFavouriteResponse.observe(this, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD && mIsClickFav) {
                Snackbar.make(deliverDetailParent, getString(R.string.remove_favourite_message), Snackbar.LENGTH_SHORT).show()
                mIsClickFav = false
            }
        })
    }

    /**
     * Display data
     *
     * @param post
     */
    private fun getResponse(post: Post) {
        postData = post
        // ServiceId
        serviceId = post.serviceId
        // Is top
        deliverDetailTop.visibility = if (post.isTop == Constant.TOP_CHECKED) View.VISIBLE else View.GONE
        // Title
        deliverDetailTitle.text = post.title
        // Service name
        deliverDetailServiceName.text = Constant.DELIVER_SERVICE_NM
        // Avatar
        deliverDetailAvatar.clipToOutline = true
        // Date
        val date = post.date.substring(0, 10).replace("-","/")
        deliverDetailDate.text = getString(R.string.post_detail_author_template, post.userName, date)
        // Price or Salary
        if (isToken()) {
            when {
                post.price != "0" -> {
                    detailPostPrice.text = resources.getString(R.string.dynamic_money_unit,
                        String.format("%,d", WindyConvertUtil.filterNumeric(post.price).toInt()))
                    detailPostPrice.setTextColor(getColor(R.color.color_pink_d14b79))
                }
                else -> {
                    detailPostPrice.text = getString(R.string.detail_post_contact_us)
                    detailPostPrice.setTextColor(getColor(R.color.color_indigo_26415d))
                }
            }
        } else {
            detailPostPrice.apply {
                text = getString(R.string.price_not_login)
                setTextColor(resources.getColorStateList(R.color.text_link_selector, null))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                typeface = Typeface.DEFAULT
                setOnClickListener(this@DeliverDetailFragment)
            }
        }
        // Road type
        detailRoadType.text = if (post.roadType == "1") Constant.ROAD_VN_JP else Constant.ROAD_JP_VN
        // Taken spot
        detailTakenAddress.text = post.addressSend
        // Receive spot
        detailReceiveAddress.text = post.addressReceive
        // Product type
        detailProductType.text = getString(R.string.detail_product_type, post.productType)
        // Product weight
        detailProductWeight.text = getString(R.string.detail_product_weight, post.weight)
        // Phone
        authorPhone = post.phone
        detailPhone.text = getString(R.string.detail_phone, post.phone)
        // Company name
        detailCompName.text = getString(R.string.detail_company_name, post.companyName)
        // note
        detailNote.text = post.note
        // Favourite
        if (userId != Constant.BLANK) {
            deliverDetailFavBtn.visibility = View.VISIBLE
            if (post.isFavourite == 1)
                deliverDetailFavBtn.setImageResource(R.drawable.ic_save_active)
            else deliverDetailFavBtn.setImageResource(R.drawable.ic_save)
        } else {
            deliverDetailFavBtn.visibility = View.GONE
        }
        deliverDetailFavLink.text = getString(R.string.like_title, post.likes.size.toString())
        // Rounded button call & SMS
        deliverDetailCallBtn.clipToOutline = true
        deliverDetailMsgBtn.clipToOutline = true
        // Handle events
        deliverDetailCmtLink.setOnClickListener(this)
        deliverDetailFavLink.setOnClickListener(this)
        deliverDetailCallBtn.setOnClickListener(this)
        deliverDetailMsgBtn.setOnClickListener(this)
        detailGmapTakenBtn.setOnClickListener(this)
        detailGmapReceivedBtn.setOnClickListener(this)
        // OnClick fav button
        var isAdded = false
        deliverDetailFavBtn.setOnClickListener {
            mIsClickFav = true
            if (post.isFavourite == 0 && !isAdded) {
                isAdded = true
                deliverDetailFavBtn.setImageResource(R.drawable.ic_save_active)
                userId?.let { postDetailViewModel.addFavourite(it, postId) }
                post.isFavourite = 1
            } else {
                isAdded = false
                deliverDetailFavBtn.setImageResource(R.drawable.ic_save)
                userId?.let { postDetailViewModel.removeFavourite(it, postId) }
                post.isFavourite = 0
            }
        }
    }

    /**
     * Handle onClick items
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.deliverDetailCmtLink -> {
                val action = DeliverDetailFragmentDirections.actionDeliverDetailFragmentToCommentFragment(postId)
                findNavController().navigate(action)
            }
            R.id.deliverDetailFavLink -> {
                val action = DeliverDetailFragmentDirections.actionDeliverDetailFragmentToLikedListFragment(postId)
                findNavController().navigate(action)
            }
            R.id.deliverDetailCallBtn -> handleAction(Constant.REQUEST_CALL)
            R.id.deliverDetailMsgBtn -> {
                if (isToken()) {
                    val action = DeliverDetailFragmentDirections
                        .actionDeliverDetailFragmentToChatDetailFragment(postData.userId,
                            postData.userName,
                            postData.userAvatar,
                            postData.phone)
                    findNavController().navigate(action)
                } else {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
            R.id.detailPostPrice -> findNavController().navigate(R.id.loginFragment)
            R.id.detailGmapTakenBtn -> detailTakenAddress.openGoogleMap()
            R.id.detailGmapReceivedBtn -> detailReceiveAddress.openGoogleMap()
        }
    }

    /**
     * Open Google Map application to show location
     * by post's address
     *
     */
    private fun TextView.openGoogleMap() {
        // Validate address filed empty or not
        if (this.text.isEmpty()) {
            showAlertDialog(getString(R.string.err_msg_empty_address_map))
            return
        }
        // Query location with Google map
        mPlaces?.openGmapApplication(context, this.text.toString())
    }

/**
     * Handle when user call author
     *
     * @param requestCd
     */
    private fun handleAction(requestCd:Int) {
        var intent = Intent()
        when(requestCd) {
            Constant.REQUEST_CALL -> {
                intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$authorPhone")
                }
            }
            Constant.REQUEST_MSG -> {
                intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$authorPhone")
                }
            }
        }
        activity?.let {context ->
            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(intent)
            }
        }

    }

    override fun initAction() {

    }

    /**
     * Get color from resources
     *
     * @param color
     */
    private fun getColor(color: Int): Int {
        var finalColor = 0
        activity?.let { context ->
            finalColor = ContextCompat.getColor(context, color)
        }
        return finalColor
    }

    /**
     * Open Image view screen
     *
     * @param list
     * @param position
     */
    private fun onClickImg(list: ArrayList<String>, position: Int) {
        val listImg = arrayOfNulls<String>(list.size)
        list.toArray(listImg)
        val action = DeliverDetailFragmentDirections.actionDeliverDetailFragmentToZoomFragment(listImg, position)
        findNavController().navigate(action)
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

    /**
     * Init pager images
     *
     */
    private fun PagerDetailAdapter.initPager(list: List<Images>) {
        this.setItem(list)
        // Set up banner list with pager adapter
        deliverDetailPager.clipToOutline = true
        deliverDetailPager.adapter = this
        // Display indicator
        displayIndicator(deliverPagerIndicator, deliverDetailPager, this.itemCount)
    }

    /**
     * Create and display indicators
     * @param targetArea Indicator layout
     * @param pager ViewPager2
     * @param indicatorCnt Adapter.count
     */
    private fun displayIndicator(targetArea: LinearLayout, pager: ViewPager2, indicatorCnt: Int) {
        // Item less than 2 can not display as pager slider
        if (indicatorCnt < 2) return
        val indicator = arrayOfNulls<ImageView>(indicatorCnt)
        for (i in 0 until indicatorCnt) {
            indicator[i] = ImageView(activity)
            indicator[i]?.setImageDrawable(
                activity?.applicationContext?.let {context ->
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.tab_indicator_default
                    )
                }
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            targetArea.addView(indicator[i], params)
        }
        // Get last position then re-display (popBackStack case)
        val lastPosition = if (pager.currentItem != -1) pager.currentItem else 0
        // Active on first index
        indicator[lastPosition]?.setImageDrawable(
            activity?.applicationContext?.let {context->
                ContextCompat.getDrawable(
                    context,
                    R.drawable.tab_indicator_selected
                )
            }
        )
        // Sync indicator with pager
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in 0 until indicatorCnt) {
                    indicator[i]?.setImageDrawable(
                        activity?.applicationContext?.let {context ->
                            ContextCompat
                                .getDrawable(context, R.drawable.tab_indicator_default)
                        }
                    )
                    indicator[position]?.setImageDrawable(
                        activity?.applicationContext?.let {context ->
                            ContextCompat
                                .getDrawable(context, R.drawable.tab_indicator_selected)
                        }
                    )
                }
            }
        })
    }
}