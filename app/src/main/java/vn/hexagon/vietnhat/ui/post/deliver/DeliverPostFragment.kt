package vn.hexagon.vietnhat.ui.post.deliver

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
import kotlinx.android.synthetic.main.fragment_post_deliver.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.DELIVER_SERVICE_ID
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentPostDeliverBinding
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
class DeliverPostFragment: MVVMBaseFragment<FragmentPostDeliverBinding, DeliverPostViewModel>(), View.OnClickListener,
    OnDialogClickListener, PhotoSelectListener, DialogThreeOptionListener, EasyPermissions.PermissionCallbacks,
    GalleryPickerListener {

    // View model
    private lateinit var deliverViewModel: DeliverPostViewModel
    // Horizontal adapter
    private lateinit var mHorizontalAdapter: HorizontalImagesAdapter
    // List images
    private var pathArray = ArrayList<String>()
    // Shared Preference
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    // Progress bar
    private var mProgressbarHandler: ProgressbarHandler? = null
    // Places
    private var mPlaces: PlacesUtils? = null
    // UserId
    var userId: String? = Constant.BLANK
    // PostId
    var postId:String = Constant.BLANK
    // Mode
    var mode = Constant.MODE_CREATE

    override fun getBaseViewModel(): DeliverPostViewModel {
        deliverViewModel =
            ViewModelProviders.of(this, viewModelFactory)[DeliverPostViewModel::class.java]
        return deliverViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        argument?.let {
            mode = DeliverPostFragmentArgs.fromBundle(it).mode
            postId = DeliverPostFragmentArgs.fromBundle(it).postId
            userId?.let { userId -> deliverViewModel.getDetailPost(userId, postId) }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_deliver)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_post_deliver

    override fun initView() {
        activity?.let { context ->
            // Progress bar init
            mProgressbarHandler = ProgressbarHandler(context)
            // Places picker init
            mPlaces = PlacesUtils(context)
            // Init spinner
            deliverPostSpinnerWay.applyCustomSpinner(
                context,
                R.array.roadType,
                R.layout.layout_common_spinner,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        // Get detail response
        deliverViewModel.deliverDetailResponse.observe(this, Observer { response ->
            getDetailResponse(response)
        })
        // Get response observe
        deliverViewModel.deliverPostResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Handle network status
        deliverViewModel.networkState.observe(this, Observer { status ->
            handleResponseNetwork(status)
        })
        // Init adapter horizontal
        mHorizontalAdapter = HorizontalImagesAdapter(::onItemClick)
        // Init images recycler view
        initImageHorizontal()
        // Pick images
        deliverPostSelectBtn.setOnClickListener(this)
        // Address take picker
        deliverPostEdAddressTaken.setOnClickListener(this)
        // Address receive picker
        deliverPostEdAddressReceived.setOnClickListener(this)
        // Commit data
        deliverPostCommitBtn.setOnClickListener(this)
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
     * Handle detail response
     *
     * @param response
     */
    private fun getDetailResponse(response: PostDetailResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            deliverPostEdAddressReceived.setText(response.data.addressReceive)
            deliverPostEdAddressTaken.setText(response.data.addressSend)
            deliverPostEdCompName.setText(response.data.companyName)
            deliverPostEdMessenger.setText(response.data.note)
            deliverPostEdPhone.setText(WindyConvertUtil.getPhoneNumber(response.data.phone))
            val countryCode = WindyConvertUtil.getStringBetween(response.data.phone, "+"," ").toInt()
            deliverCountryCodePicker.setCountryForPhoneCode(countryCode)
            deliverPostEdPrice.setText(response.data.price)
            deliverPostEdTitle.setText(response.data.title)
            deliverPostEdType.setText(response.data.productType)
            deliverPostEdWeight.setText(response.data.weight)
            deliverPostSpinnerWay.setSelection(response.data.roadType.toInt().minus(1))
            deliverPostPinCheck.isChecked = response.data.isTop == Constant.TOP_CHECKED
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
     * Init images recycler view
     *
     */
    private fun initImageHorizontal() {
        activity?.let { context ->
            // Set up recyclerView
            deliverPostRecyclerImg.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = mHorizontalAdapter
            }
        }
    }

    /**
     * Handle events for onClick for views
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.deliverPostEdAddressTaken -> mPlaces?.displayPlacePicker(this, Constant.REQUEST_CD_PLACE)
            R.id.deliverPostEdAddressReceived -> mPlaces?.displayPlacePicker(this, Constant.REQUEST_CD_2ND_PLACE)
            R.id.deliverPostSelectBtn -> addImages()
            R.id.deliverPostCommitBtn -> processSendDataPost()
        }
    }

    /**
     * Get value of road type
     *
     * @return "1" if road type is VN - JP
     */
    private fun getRoadType(): String {
        return if (deliverPostSpinnerWay.selectedItemPosition == 0) {
            "1"
        } else {
            "2"
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
                    deliverViewModel.requestDataPost(
                        userId,
                        DELIVER_SERVICE_ID,
                        WindyConvertUtil.convertPhoneNumber(deliverCountryCodePicker, deliverPostEdPhone),
                        getRoadType(),
                        if (deliverPostPinCheck.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        mHorizontalAdapter.images
                    )
                }
                Constant.MODE_EDIT -> {
                    deliverViewModel.editDataPost(
                        userId,
                        postId,
                        WindyConvertUtil.convertPhoneNumber(deliverCountryCodePicker, deliverPostEdPhone),
                        getRoadType(),
                        if (deliverPostPinCheck.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
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
        val arrayFields = arrayOf<EditText>(deliverPostEdTitle, deliverPostEdCompName, deliverPostEdAddressTaken,
            deliverPostEdAddressReceived, deliverPostEdType, deliverPostEdWeight, deliverPostEdPrice, deliverPostEdPhone, deliverPostEdMessenger)
        val validate = validateFieldsGlobal(arrayFields, deliverCountryCodePicker, deliverPostEdPhone, null, deliverPostParent)
        if (!validate) return
        // Check purchase
        if (deliverPostPinCheck.isChecked) {
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
            Snackbar.make(deliverPostParent, getString(R.string.limit_photo_message), Snackbar.LENGTH_SHORT).show()
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
     * Listener permission, Open gallery/ camera
     * immediately after permissions was granted
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
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
            Constant.REQUEST_CD_PLACE -> mPlaces?.receivedAddressProcess(resultCode, data, deliverPostEdAddressTaken)
            Constant.REQUEST_CD_2ND_PLACE -> mPlaces?.receivedAddressProcess(resultCode, data, deliverPostEdAddressReceived)
            Constant.REQUEST_GALLERY -> {
                val selectedImg = data?.data
                try {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
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
                        val columnIndex =
                            cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
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
        val arrayFields = arrayOf<EditText>(deliverPostEdTitle, deliverPostEdCompName, deliverPostEdAddressTaken,
            deliverPostEdAddressReceived, deliverPostEdType, deliverPostEdWeight, deliverPostEdPrice, deliverPostEdPhone, deliverPostEdMessenger)
        val validate = validateFieldsGlobal(arrayFields, deliverCountryCodePicker, deliverPostEdPhone, null, deliverPostParent)
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