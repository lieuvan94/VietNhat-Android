package vn.hexagon.vietnhat.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_post_detail.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.detail.Images
import vn.hexagon.vietnhat.data.model.job.Job
import vn.hexagon.vietnhat.data.model.job.JobResponse
import vn.hexagon.vietnhat.data.model.news.Subject
import vn.hexagon.vietnhat.data.model.news.SubjectResponse
import vn.hexagon.vietnhat.data.model.post.Post
import vn.hexagon.vietnhat.databinding.FragmentPostDetailBinding
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
 * Create on：2019-09-29
 * =====================================================
 */
class PostDetailFragment: MVVMBaseFragment<FragmentPostDetailBinding, PostDetailViewModel>(),
    View.OnClickListener {
    // Action bar
    private val actionBar: SimpleActionBar? by lazy {
        baseActionBar as? SimpleActionBar
    }
    // Account Info View Model
    private lateinit var postDetailViewModel: PostDetailViewModel
    // Author phone number
    private var authorPhone = Constant.BLANK
    // Shared preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // Product Adapter
    private lateinit var productAdapter: ProductDetailAdapter
    // ServiceId
    private var serviceId = Constant.BLANK
    // UserId
    private var userId:String? = Constant.BLANK
    // Author ID
    private var authorId:String? = Constant.BLANK
    // Post ID
    private var postId:String = Constant.BLANK
    // List job type
    private var jobTypeList = ArrayList<Job>()
    // List subject
    private var subjectList = ArrayList<Subject>()
    // Is Add favourite flag
    private var mIsClick = false
    // Places
    private var mPlaces: PlacesUtils? = null

    private lateinit var postData: Post

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
            postId = PostDetailFragmentArgs.fromBundle(it).postId
            // Request author info
            userId?.let { userId -> postDetailViewModel.getDetailPost(userId, postId) }
            // Request product list
            postDetailViewModel.getProductListPost(postId)
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity)

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_post_detail

    override fun initView() {
        activity?.let { context ->
            // Places picker init
            mPlaces = PlacesUtils(context)
        }
        // Current userId
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        authorId = arguments?.let { PostDetailFragmentArgs.fromBundle(it).userId }
        // Add favourite response
        postDetailViewModel.favouriteResponse.observe(this, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD) {
                if (mIsClick) {
                    Snackbar.make(
                        postDetailParent,
                        getString(R.string.add_favourite_message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    mIsClick = false
                }
            }
        })
        // Remove favourite response
        postDetailViewModel.unFavouriteResponse.observe(this, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD) {
                if (mIsClick) {
                    Snackbar.make(
                        postDetailParent,
                        getString(R.string.remove_favourite_message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    mIsClick = false
                }
            }
        })
        // Init recyclerView
        initRecyclerView()
        // Detail post response
        postDetailViewModel.detailPostResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.errorId == Constant.RESPOND_CD) {
                activity?.let { context ->
                    val pagerAdapter = PagerDetailAdapter(context, ::onClickImg)
                    getResponse(response.data)
                    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        DebugLog.e("SIZE: " + response?.data?.imagesList?.size.toString())
                        // Image cover
                        pagerAdapter.initPager(response.data.imagesList)
                    }
                    initActionBar()
                }
            } else {
                DebugLog.e(response.message)
            }
        })
        // Expand all products
        detailProductViewAllLink.setOnClickListener(this)
    }

    /**
     * Handle response subject
     *
     * @param response
     * @param post
     */
    private fun getSubjectResponse(response: SubjectResponse?, post: Post) {
        if (response?.errorId == Constant.RESPOND_CD) {
            subjectList.addAll(response.subject)
            postDetailSubject.apply {
                visibility = View.VISIBLE
                val position = response.subject.indexOfFirst { it.id == post.subjectId }
                text = response.subject[position].name
            }
        } else {
            DebugLog.e(response?.message.toString())
        }
    }

    /**
     * Get job type response
     *
     * @param response
     * @param post
     */
    private fun getJobTypeResponse(response: JobResponse?, post: Post) {
        if (response?.errorId == Constant.RESPOND_CD){
            postDetailJobType.apply {
                visibility = View.VISIBLE
                val position = response.job.indexOfFirst { it.jobId == post.jobTypeId }
                text = getString(R.string.detail_job_type_template, response.job[position].jobName)
            }
        } else {
            showAlertDialog("Error", response?.message, "OK")
        }
    }

    /**
     * Display action bar
     *
     */
    private fun initActionBar() {
        // Init action bar
        actionBar?.apply {
            simpleTitleText = getTitleScreen(serviceId)
            leftButtonVisible = true
            rightButtonVisible = userId == authorId
            rightButtonResource = R.drawable.ic_edit
            leftActionBarButton.setOnClickListener {
                findNavController().popBackStack()
            }
            rightActionBarButton.setOnClickListener {
                specificEditDestination(serviceId)
            }
        }
    }

    /**
     * Init recyclerView and product adapter
     *
     */
    private fun initRecyclerView() {
        productAdapter = ProductDetailAdapter(ArrayList())
        activity?.let { context ->
            detailItemRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = productAdapter
            }
        }
        // Product list response
        postDetailViewModel.productListResponse.observe(this, Observer { response ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (response.data.isNotEmpty()) {
                    productAdapter.clearAllItem()
                    detailProductListArea.visibility = View.VISIBLE
                    // Submit product list
                    DebugLog.e("${response.errorId} - ${response.message}")
                    productAdapter.insertItem(response.data)
                    if (productAdapter.list.size > 5) {
                        detailProductViewAllLink.visibility = View.VISIBLE
                        detailProductViewAllLink.text = getString(R.string.reveal_all_product_link, productAdapter.list.size.toString())
                    } else {
                        detailProductViewAllLink.visibility = View.GONE
                    }
                }
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
        postDetailTopIcon.visibility = if (post.isTop == Constant.TOP_CHECKED) View.VISIBLE else View.GONE
        // Service name
        postDetailServiceName.text = WindyConvertUtil.getServiceName(post.serviceId)
        // Avatar
        postDetailAvatar.clipToOutline = true
        // Date
        val date = post.date.substring(0, 10).replace("-","/")
        postListDetailDate.text = getString(R.string.post_detail_author_template, post.userName, date)
        // Address
        if (post.address.isNotEmpty()) {
            detailGmapBtn.visibility = View.VISIBLE
            postDetailAddress.visibility = View.VISIBLE
        } else {
            detailGmapBtn.visibility = View.GONE
            postDetailAddress.visibility = View.GONE
        }
        // Subject
        if (post.serviceId == Constant.NEW_SERVICE_ID) {
            // Get news category
            postDetailViewModel.requestNewsCategory()
            // News category response
            postDetailViewModel.subjectResponse.observe(this, Observer { response ->
                getSubjectResponse(response, post)
            })
            detailCallBtn.visibility = View.GONE
        } else {
            detailCallBtn.visibility = View.VISIBLE
            postDetailSubject.visibility = View.GONE
        }
        // Job type
        if (post.serviceId == Constant.JOB_SERVICE_ID) {
            // Get job type
            postDetailViewModel.requestJobType()
            // Get job category
            postDetailJobCategory.apply {
                visibility = View.VISIBLE
                text = getString(R.string.detail_job_category_template, post.jobCategory)
            }
            // Job type response
            postDetailViewModel.jobTypeResponse.observe(this, Observer { response ->
                getJobTypeResponse(response, post)
            })
        } else {
            postDetailJobType.visibility = View.GONE
            postDetailJobCategory.visibility = View.GONE
        }
        // Price or Salary
        if (isToken()) {
            when {
                post.price != "0" || post.salary != "0" -> {
                    detailPostPrice.text = processPriceUnit(post)
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
                setOnClickListener(this@PostDetailFragment)
            }
        }
        // Phone
        if (post.phone.isNotEmpty()) {
            postDetailPhone.visibility = View.VISIBLE
            authorPhone = post.phone
            postDetailPhone.text = getString(R.string.detail_phone, post.phone)
        } else {
            postDetailPhone.visibility = View.GONE
        }
        // Note
        if (post.note.isNotEmpty()) {
            postDetailNoteIcon.visibility = View.VISIBLE
            postDetailNote.visibility = View.VISIBLE
            postDetailNote.text = post.note
        } else {
            postDetailNoteIcon.visibility = View.GONE
            postDetailNote.visibility = View.GONE
        }
        // Favourite
        if (userId != Constant.BLANK) {
            postDetailFavBtn.visibility = View.VISIBLE
            if (post.isFavourite == 1)
                postDetailFavBtn.setImageResource(R.drawable.ic_save_active)
            else postDetailFavBtn.setImageResource(R.drawable.ic_save)
        } else {
            postDetailFavBtn.visibility = View.GONE
        }
        postDetailFavLink.text = getString(R.string.like_title, post.likes.size.toString())
        // Rounded button call & SMS
        detailCallBtn.clipToOutline = true
        detailMsgBtn.clipToOutline = true
        // Handle events
        postDetailCmtLink.setOnClickListener(this)
        postDetailFavLink.setOnClickListener(this)
        detailCallBtn.setOnClickListener(this)
        detailMsgBtn.setOnClickListener(this)
        detailGmapBtn.setOnClickListener(this)
        // OnClick fav button
        var isAdded = false
        postDetailFavBtn.setOnClickListener {
            mIsClick = true
            if (post.isFavourite == 0 && !isAdded) {
                isAdded = true
                postDetailFavBtn.setImageResource(R.drawable.ic_save_active)
                userId?.let { postDetailViewModel.addFavourite(it, postId) }
                post.isFavourite = 1
            } else {
                isAdded = false
                postDetailFavBtn.setImageResource(R.drawable.ic_save)
                userId?.let { postDetailViewModel.removeFavourite(it, postId) }
                post.isFavourite = 0
            }
        }
    }

    /**
     * Init pager images
     *
     */
    private fun PagerDetailAdapter.initPager(list: List<Images>) {
        this.setItem(list)
        // Set up banner list with pager adapter
        detailInfinityPager.clipToOutline = true
        detailInfinityPager.adapter = this
        // Display indicator
        displayIndicator(homeInfinityIndicator, detailInfinityPager, this.itemCount)
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

    /**
     * Handle result of price/salary with unit money
     *
     * @param post
     * @return price/salary with unit money
     */
    private fun processPriceUnit(post: Post): CharSequence? {
        return when(post.serviceId) {
            Constant.JOB_SERVICE_ID -> getPriceUnitByJobType(post)
            Constant.VISA_SERVICE_ID,
            Constant.ADS_SERVICE_ID -> resources.getString(R.string.dynamic_money_unit,
                String.format("%,d", WindyConvertUtil.filterNumeric(post.price).toInt()))
            Constant.TRAVEL_SERVICE_ID -> resources.getString(R.string.dynamic_price_per_people,
                String.format("%,d", WindyConvertUtil.filterNumeric(post.price).toInt()))
            Constant.TRANSLATOR_SERVICE_ID -> getPriceUnitByType(post)
            else -> Constant.BLANK
        }
    }

    /**
     * Get unit price by translate type
     *
     * @param post
     * @return unit price
     */
    private fun getPriceUnitByJobType(post: Post): CharSequence? {
        val a = when(post.salaryType) {
            "1" -> R.string.dynamic_money_per_hour
            "2" -> R.string.dynamic_money_per_month
            else -> R.string.dynamic_money_per_hour
        }
        return resources.getString(a, String.format("%,d", WindyConvertUtil.filterNumeric(post.salary).toInt()))
    }

    /**
     * Get unit price by translate type
     *
     * @param post
     * @return unit price
     */
    private fun getPriceUnitByType(post: Post): CharSequence? {
        val a = when(post.translateType) {
            "1" -> R.string.dynamic_money_per_hour
            "2" -> R.string.dynamic_money_per_month
            else -> R.string.dynamic_money_per_hour
        }
        return resources.getString(a, String.format("%,d", WindyConvertUtil.filterNumeric(post.price).toInt()))
    }

    /**
     * Handle onClick items
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.postDetailCmtLink -> {
                val action = PostDetailFragmentDirections.actionPostDetailFragmentToCommentFragment(postId)
                findNavController().navigate(action)
            }
            R.id.postDetailFavLink -> {
                val action = PostDetailFragmentDirections.actionPostDetailFragmentToLikedListFragment(postId)
                findNavController().navigate(action)
            }
            R.id.detailCallBtn -> handleAction(Constant.REQUEST_CALL)
            R.id.detailMsgBtn -> {
                if (isToken()) {
                    val action = PostDetailFragmentDirections
                        .actionPostDetailFragmentToChatDetailFragment(postData.userId,
                            postData.userName,
                            postData.userAvatar,
                            postData.phone)
                    findNavController().navigate(action)
                } else {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
            R.id.detailPostPrice -> findNavController().navigate(R.id.loginFragment)
            R.id.detailProductViewAllLink -> {
                detailProductListArea.transitionName
                productAdapter.isRevealAllItem = true
                productAdapter.notifyDataSetChanged()
                detailProductViewAllLink.visibility = View.GONE
            }
            R.id.detailGmapBtn -> openGoogleMap()
        }
    }

    /**
     * Open Google Map application to show location
     * by post's address
     *
     */
    private fun openGoogleMap() {
        // Validate address filed empty or not
        if (postDetailAddress.text.isEmpty()) {
            showAlertDialog(getString(R.string.err_msg_empty_address_map))
            return
        }
        // Query location with Google map
        mPlaces?.openGmapApplication(context, postDetailAddress.text.toString())
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
     * Correct destination when edit by author
     *
     * @param serviceId
     */
    private fun specificEditDestination(serviceId:String) {
            when(serviceId) {
            "13" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToMartPostFragment(postId, Constant.MODE_EDIT))
            "12" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToJobPostFragment(postId, Constant.MODE_EDIT))
            "11" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToTransPostFragment(postId, Constant.MODE_EDIT))
            "9" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToTravelPostFragment(postId, Constant.MODE_EDIT))
            "8" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToCarPostFragment(postId, Constant.MODE_EDIT))
            "7" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToRestaurantPostFragment(postId, Constant.MODE_EDIT))
            "6" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToSpaPostFragment(postId, Constant.MODE_EDIT))
            "5" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToPhonePostFragment(postId, Constant.MODE_EDIT))
            "4" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToVisaPostFragment(postId, Constant.MODE_EDIT))
            "1" -> findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToAdsPostFragment(postId, Constant.MODE_EDIT))
        }
    }

    /**
     * Get title screen by serviceId
     *
     * @param serviceId
     * @return
     */
    private fun getTitleScreen(serviceId: String): String {
        when(serviceId) {
            "13" -> return getString(R.string.create_post_mart)
            "12" -> return getString(R.string.create_post_job)
            "11" -> return getString(R.string.create_post_translator)
            "10" -> return getString(R.string.create_post_news)
            "9" -> return getString(R.string.create_post_travel)
            "8" -> return getString(R.string.create_post_car)
            "7" -> return getString(R.string.create_post_restaurant)
            "6" -> return getString(R.string.create_post_spa)
            "5" -> return getString(R.string.create_post_phone)
            "4" -> return getString(R.string.create_post_visa)
            "2" -> return getString(R.string.create_post_support)
            "1" -> return getString(R.string.create_post_ads)
        }
        return Constant.BLANK
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
     * Transition to preview image screen
     *
     * @param list
     * @param position
     */
    private fun onClickImg(list: ArrayList<String>, position: Int) {
        val listImg = arrayOfNulls<String>(list.size)
        list.toArray(listImg)
        val action = PostDetailFragmentDirections.actionPostDetailFragmentToZoomFragment(listImg, position)
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
}