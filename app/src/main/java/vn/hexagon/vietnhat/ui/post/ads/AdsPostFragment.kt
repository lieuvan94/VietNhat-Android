package vn.hexagon.vietnhat.ui.post.ads

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_post_ads.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.ADS_SERVICE_ID
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentPostAdsBinding
import vn.hexagon.vietnhat.ui.dialog.*
import vn.hexagon.vietnhat.ui.gallery.GalleryPickerFragment
import vn.hexagon.vietnhat.ui.gallery.GalleryPickerListener
import vn.hexagon.vietnhat.ui.list.HorizontalImagesAdapter
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import vn.hexagon.vietnhat.ui.post.OnDialogClickListener
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * Created by VuNBT on 9/22/2019.
 */
class AdsPostFragment: MVVMBaseFragment<FragmentPostAdsBinding, AdsPostViewModel>(), View.OnClickListener, OnDialogClickListener,
    PhotoSelectListener, DialogThreeOptionListener, EasyPermissions.PermissionCallbacks,
    GalleryPickerListener {

    // View model
    private lateinit var adsViewModel: AdsPostViewModel
    // Horizontal adapter
    private lateinit var mHorizontalAdapter: HorizontalImagesAdapter
    // List images
    private var pathArray = ArrayList<String>()
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // UserId
    var userId:String? = Constant.BLANK
    // PostId
    var postId:String = Constant.BLANK
    // Mode
    var mode = Constant.MODE_CREATE
    // Progress bar
    private var mProgressbarHandler: ProgressbarHandler? = null
    // Places
    private var mPlaces: PlacesUtils? = null

    override fun getBaseViewModel(): AdsPostViewModel {
        adsViewModel =
            ViewModelProviders.of(this, viewModelFactory)[AdsPostViewModel::class.java]
        return adsViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        argument?.let {
            mode = AdsPostFragmentArgs.fromBundle(it).mode
            postId = AdsPostFragmentArgs.fromBundle(it).postId
            userId?.let { userId -> adsViewModel.getDetailPost(userId, postId) }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_ads)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_post_ads

    override fun initView() {
        activity?.let { context ->
            // Progress bar init
            mProgressbarHandler = ProgressbarHandler(context)
            // Places picker init
            mPlaces = PlacesUtils(context)
        }
        // Get detail response observe
        adsViewModel.adsDetailResponse.observe(this, Observer { response ->
            getDetailResponse(response)
        })
        // Get edit response observe
        adsViewModel.adsEditResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Get response observe
        adsViewModel.adsPostResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Handle network status
        adsViewModel.networkState.observe(this, Observer { status ->
            handleResponseNetwork(status)
        })
        // Init adapter horizontal
        mHorizontalAdapter = HorizontalImagesAdapter(::onItemClick)
        // Init images recycler view
        initImageHorizontal()
        // Address picker
        adsPostEdAddress.setOnClickListener(this)
        // Pick images
        adsPostSelectBtn.setOnClickListener(this)
        // Commit data
        adsPostCommitBtn.setOnClickListener(this)
    }

    /**
     * Handle network state with progress bar
     *
     * @param response
     */
    private fun handleResponseNetwork(response: NetworkState) {
        when(response) {
            NetworkState.LOADING -> mProgressbarHandler?.show()
            NetworkState.LOADED -> mProgressbarHandler?.hide()
            NetworkState.ERROR -> mProgressbarHandler?.hide()
        }
    }

    /**
     * Get detail response
     *
     * @param response
     */
    private fun getDetailResponse(response: PostDetailResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            adsPostEdContent.setText(response.data.content)
            adsPostEdMessenger.setText(response.data.note)
            adsPostEdPhone.setText(WindyConvertUtil.getPhoneNumber(response.data.phone))
            val countryCode = WindyConvertUtil.getStringBetween(response.data.phone, "+"," ").toInt()
            adsCountryCodePicker.setCountryForPhoneCode(countryCode)
            adsPostEdTitle.setText(response.data.title)
            adsPostPinCheck.isChecked = response.data.isTop == Constant.TOP_CHECKED
            if (response.data.imagesList.isNotEmpty()) {
                response.data.imagesList.forEach {
                    pathArray.add(it.img)
                }
                mHorizontalAdapter.addImages(pathArray)
            }
        } else {
            showAlertDialog("Error", response?.message, "OK")
        }
    }

    /**
     * Handle events for onClick for views
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.adsPostEdAddress -> mPlaces?.displayPlacePicker(this, Constant.REQUEST_CD_PLACE)
            R.id.adsPostSelectBtn -> addImages()
            R.id.adsPostCommitBtn -> processSendDataPost()
        }
    }

    /**
     * Init images recycler view
     *
     */
    private fun initImageHorizontal() {
        activity?.let { context ->
            // Set up recyclerView
            adsPostRecyclerImg.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = mHorizontalAdapter
            }
        }
    }

    /**
     * Send data post with parameters
     *
     */
    private fun sendDataPost() {
        userId?.let { userId ->
            when(mode) {
                Constant.MODE_CREATE -> {
                    adsViewModel.requestDataPost(
                        userId,
                        ADS_SERVICE_ID,
                        WindyConvertUtil.convertPhoneNumber(adsCountryCodePicker, adsPostEdPhone),
                        if (adsPostPinCheck.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        mHorizontalAdapter.images
                    )
                }
                Constant.MODE_EDIT -> {
                    adsViewModel.editDataPost(
                        userId,
                        postId,
                        WindyConvertUtil.convertPhoneNumber(adsCountryCodePicker, adsPostEdPhone),
                        if (adsPostPinCheck.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        mHorizontalAdapter.images
                    )
                }
            }
        }
    }

    /**
     * Display confirm TOP dialog when check to checkbox
     *
     */
    private fun processSendDataPost() {
        // Validate
        val arrayFields = arrayOf<EditText>(adsPostEdTitle, adsPostEdAddress, adsPostEdPhone, adsPostEdPrice, adsPostEdContent, adsPostEdMessenger)
        val validate = validateFieldsGlobal(arrayFields, adsCountryCodePicker, adsPostEdPhone, null, adsPostParent)
        if (!validate) return
        // Check purchase
        if (adsPostPinCheck.isChecked) {
            fragmentManager?.let {
                val dialog = ConfirmTopDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(it, "CreateTopDialog")
            }
        } else {
            sendDataPost()
        }
    }

    /**
     * Response from server after commit post
     *
     * @param response
     */
    private fun getResponse(response: PostResponse) {
        DebugLog.e("Commit Status: ${response.errorId}")
        if (response.errorId == Constant.RESPOND_CD) {
            // Display commit done dialog
            fragmentManager?.let { ConfirmDoneDialogFragment().show(it, "CreateDoneDialog") }
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Open gallery or camera
     *
     */
    private fun addImages() {
        if (mHorizontalAdapter.itemCount == mHorizontalAdapter.LIMIT_SIZE) {
            Snackbar.make(adsPostParent, getString(R.string.limit_photo_message), Snackbar.LENGTH_SHORT).show()
            return
        }
        fragmentManager?.let {
            val dialog = DialogSelectPhotoFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(it, "SelectPhotoDialog")
        }
    }

    /**
     * Handle event select platform
     *
     * @param isCamera
     */
    override fun onSelectPhoto(isCamera: Boolean) {
        if (isCamera) {
            insertImages(Constant.REQUEST_CAMERA)
        } else {
            requestPermissionStorage()
        }
    }

    /**
     * Handle when permission denied
     *
     * @param requestCode
     * @param perms
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // Do nothing
    }

    /**
     * Handle when permission granted
     *
     * @param requestCode
     * @param perms
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when(requestCode) {
            Constant.REQUEST_CD_GRANT_PERMISSION -> {
                this.openGallery(false, Constant.REQUEST_CD_PERMISSION)
            }
            Constant.REQUEST_GALLERY_MULTI_PICK -> {
                insertMultiImages()
            }
            Constant.REQUEST_CD_GRANT_PERMISSION_CAMERA -> {
                activity?.let { context -> this.processTakePhoto(context) }
            }
        }
    }

    /**
     * Activity Result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_CD_PLACE -> mPlaces?.receivedAddressProcess(resultCode, data, adsPostEdAddress)
            Constant.REQUEST_GALLERY -> {
                val selectedImg = data?.data
                try {
                    val filePathColumn =  arrayOf(MediaStore.Images.Media.DATA)
                    // Get the cursor
                    if (selectedImg != null) {

                        val cursor = context?.contentResolver?.query(
                            selectedImg,
                            filePathColumn,
                            null,
                            null,
                            null
                        )

                        // Move to first row
                        cursor?.moveToFirst()
                        // Get the column index of MediaStore.Images.Media.DATA
                        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        val imgDecorableString = columnIndex?.let { cursor.getString(it) }
                        // Close cursor
                        cursor?.close()
                        // Replace image name with new one then add to the list
                        imgDecorableString?.let {
                            pathArray[mHorizontalAdapter.mPosition] = it
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Constant.REQUEST_CAMERA -> {
                val imageFile = File(currentPhotoPath)
                // Save to gallery
                activity?.let { context ->
                    currentPhotoPath?.let { path ->
                        WindyConvertUtil.saveToGallery(
                            context,
                            path
                        )
                    }
                }
                // Send photo path to display on product list
                pathArray.add(imageFile.path)
            }
        }
        // Add images to adapter then display them
        mHorizontalAdapter.addImages(pathArray)
    }

    /**
     * Handle onClick dialog edit/delete
     *
     * @param isDelete
     */
    override fun onModify(isDelete: Boolean) {
        if (isDelete) {
            mHorizontalAdapter.removeImages()
            pathArray.removeAt(mHorizontalAdapter.mPosition)
        } else {
            this.openGallery(false, Constant.REQUEST_GALLERY)
        }
    }

    /**
     * Handle onClick image item
     *
     */
    private fun onItemClick() {
        fragmentManager?.let {
            val dialog = DialogThreeOptionsFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(it, "SelectOptionDialog")
        }
    }

    /**
     * Commit post after purchased
     *
     */
    override fun onClickPurchaseButton() {
        // Validate
        val arrayFields = arrayOf<EditText>(adsPostEdTitle, adsPostEdPhone, adsPostEdContent, adsPostEdMessenger)
        val validate = validateFieldsGlobal(arrayFields, adsCountryCodePicker, adsPostEdPhone, null, adsPostParent)
        if (!validate) return
        // Commit post
        sendDataPost()
    }

    override fun initAction() {
    }

    /**
     * Received images from bottom sheet
     *
     * @param imgSelected
     */
    override fun onSendImages(imgSelected: ArrayList<String>) {
        // Pass data to pathArray for edit/delete image
        pathArray = imgSelected
        // Bind data on horizontal recycler view
        mHorizontalAdapter.apply {
            images.clear()
            addImages(imgSelected)
            notifyDataSetChanged()
        }
    }

    /**
     * Request permission to access storage
     *
     */
    private fun requestPermissionStorage() {
        val permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE
        activity?.let { context ->
            if (EasyPermissions.hasPermissions(context, permissionRead, permissionWrite)) {
                insertMultiImages()
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.regrant_access_permission),
                    Constant.REQUEST_GALLERY_MULTI_PICK, permissionRead, permissionWrite
                )
            }
        }
    }

    /**
     * Insert multi images by bottom sheet
     *
     */
    private fun insertMultiImages() {
        fragmentManager?.let {
            val dialog = GalleryPickerFragment(Constant.LIMIT_IMG)
            dialog.setTargetFragment(this, 0)
            dialog.show(it, "SelectImgBottomSheet")
        }
    }
}