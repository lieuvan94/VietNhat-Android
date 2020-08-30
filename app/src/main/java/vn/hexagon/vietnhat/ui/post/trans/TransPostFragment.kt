package vn.hexagon.vietnhat.ui.post.trans

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_post_translate.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.RESPOND_CD
import vn.hexagon.vietnhat.constant.Constant.TRANSLATOR_SERVICE_ID
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentPostTranslateBinding
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
 * Created by VuNBT on 9/14/2019.
 */
class TransPostFragment: MVVMBaseFragment<FragmentPostTranslateBinding, TransPostViewModel>(),
    View.OnClickListener, OnDialogClickListener, PhotoSelectListener,
    DialogThreeOptionListener, EasyPermissions.PermissionCallbacks,
    GalleryPickerListener {

    // View model
    private lateinit var transPostViewModel: TransPostViewModel
    // List images
    private var pathArray = ArrayList<String>()
    // Horizontal adapter
    private lateinit var mHorizontalAdapter: HorizontalImagesAdapter
    // Shared Preference
    @Inject lateinit var sharedPreferences: SharedPreferences
    // Progress bar
    private var mProgressbarHandler: ProgressbarHandler? = null
    // Places
    private var mPlaces: PlacesUtils? = null
    // UserId
    var userId:String? = Constant.BLANK
    // Post ID
    var postId = Constant.BLANK
    // Mode
    var mode = 0

    override fun getBaseViewModel(): TransPostViewModel {
        transPostViewModel = ViewModelProviders.of(this, viewModelFactory)[TransPostViewModel::class.java]
        return transPostViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val dataBinding = getBaseViewDataBinding()
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        argument?.let {
            mode = TransPostFragmentArgs.fromBundle(it).mode
            postId = TransPostFragmentArgs.fromBundle(it).postId
            if (mode == Constant.MODE_EDIT && userId != null) {
                transPostViewModel.getDetailPost(userId!!, postId)
            }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_translator)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_post_translate

    override fun initAction() {
    }

    override fun initView() {
        // Init progress bar
        activity?.let { context ->
            // Progress bar init
            mProgressbarHandler = ProgressbarHandler(context)
            // Places picker init
            mPlaces = PlacesUtils(context)
            // Init spinner
            postTransSelectPriceUnit.applyCustomSpinner(
                context,
                R.array.salaryType,
                R.layout.layout_common_spinner,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        // Get response observe
        transPostViewModel.transPostResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Get detail post info
        transPostViewModel.transDetailResponse.observe(this, Observer { response ->
            handleResponseDetail(response)
        })
        // Get edit response observe
        transPostViewModel.transEditPostResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Handle network status
        transPostViewModel.networkState.observe(this, Observer { status ->
            handleResponseNetwork(status)
        })
        // Init adapter horizontal
        mHorizontalAdapter = HorizontalImagesAdapter(::onItemClick)
        // Init images recycler view
        initImageHorizontal()
        // Pick images
        postTransPickBtn.setOnClickListener(this)
        // Address picker
        postTransEdAddress.setOnClickListener(this)
        // Commit data
        postTransCommitBtn.setOnClickListener(this)
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
     * Handle Response detail before edit
     *
     * @param response
     */
    private fun handleResponseDetail(response: PostDetailResponse?) {
        if (response?.errorId == RESPOND_CD) {
            DebugLog.e("${response.errorId} - ${response.message}")
            postTransEdTitle.setText(response.data.title)
            postTransEdAddress.setText(response.data.address)
            postTransEdPrice.setText(response.data.price)
            postTransEdPhone.setText(WindyConvertUtil.getPhoneNumber(response.data.phone))
            val countryCode = WindyConvertUtil.getStringBetween(response.data.phone, "+"," ").toInt()
            translatorCountryCodePicker.setCountryForPhoneCode(countryCode)
            postTransEdContent.setText(response.data.content)
            postTransEdNote.setText(response.data.note)
            postTransTopCb.isChecked = response.data.isTop == Constant.TOP_CHECKED
            postTransSelectPriceUnit.setSelection(response.data.translateType.toInt().minus(1), true)
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
     * Get Translate Id by selection
     *
     * @return translate type id
     */
    private fun getTranslateType(): String {
        return when(postTransSelectPriceUnit.selectedItemPosition) {
            0, 1 -> postTransSelectPriceUnit.selectedItemPosition.plus(1).toString()
            else -> "1"
        }
    }

    /**
     * Handle events for onClick for views
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.postTransEdAddress -> mPlaces?.displayPlacePicker(this, Constant.REQUEST_CD_PLACE)
            R.id.postTransPickBtn -> insertImages()
            R.id.postTransCommitBtn -> processSendDataPost()
        }
    }

    /**
     * Init images recycler view
     *
     */
    private fun initImageHorizontal() {
        activity?.let { context ->
            // Set up recyclerView
            translatePostRecyclerImg.apply {
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
        userId?.let {
            when(mode) {
                Constant.MODE_CREATE -> {
                    transPostViewModel.sendDataPost(
                        it,
                        TRANSLATOR_SERVICE_ID,
                        transPostViewModel.title,
                        transPostViewModel.address,
                        transPostViewModel.price,
                        getTranslateType(),
                        WindyConvertUtil.convertPhoneNumber(translatorCountryCodePicker, postTransEdPhone),
                        transPostViewModel.content,
                        transPostViewModel.note,
                        if (postTransTopCb.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        mHorizontalAdapter.images
                    )
                }
                Constant.MODE_EDIT -> {
                    transPostViewModel.editDataPost(
                        it,
                        postId,
                        transPostViewModel.title,
                        transPostViewModel.address,
                        transPostViewModel.price,
                        getTranslateType(),
                        WindyConvertUtil.convertPhoneNumber(translatorCountryCodePicker, postTransEdPhone),
                        transPostViewModel.content,
                        transPostViewModel.note,
                        if (postTransTopCb.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
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
        val arrayFields = arrayOf<EditText>(postTransEdTitle, postTransEdAddress, postTransEdPrice, postTransEdPhone, postTransEdContent, postTransEdNote)
        val validate = validateFieldsGlobal(arrayFields, translatorCountryCodePicker, postTransEdPhone, null, transPostParent)
        if (!validate) return
        // Check top post
        if (postTransTopCb.isChecked) {
            fragmentManager?.let {
                val dialog = ConfirmTopDialogFragment()
                dialog.setTargetFragment(this, 0)
                dialog.show(it, "CreateTransDialog")
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
    private fun getResponse(response:PostResponse) {
        DebugLog.e("Commit Status: ${response.errorId}")
        if (response.errorId == RESPOND_CD) {
            // Display commit done dialog
            fragmentManager?.let { ConfirmDoneDialogFragment().show(it, "CreateTranslatorDialog") }
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Open gallery or camera
     *
     */
    private fun insertImages() {
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
            Constant.REQUEST_CD_PLACE -> mPlaces?.receivedAddressProcess(resultCode, data, postTransEdAddress)
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
        val arrayFields = arrayOf<EditText>(postTransEdTitle, postTransEdAddress, postTransEdPrice, postTransEdPhone, postTransEdContent, postTransEdNote)
        val validate = validateFieldsGlobal(arrayFields, translatorCountryCodePicker, postTransEdPhone, null, transPostParent)
        if (!validate) return
        // Commit post
        sendDataPost()
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