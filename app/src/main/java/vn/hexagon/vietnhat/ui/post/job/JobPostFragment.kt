package vn.hexagon.vietnhat.ui.post.job

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_post_job.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.JOB_SERVICE_ID
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.job.Job
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentPostJobBinding
import vn.hexagon.vietnhat.ui.dialog.*
import vn.hexagon.vietnhat.ui.gallery.GalleryPickerFragment
import vn.hexagon.vietnhat.ui.gallery.GalleryPickerListener
import vn.hexagon.vietnhat.ui.list.HorizontalImagesAdapter
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import vn.hexagon.vietnhat.ui.post.OnDialogClickListener
import java.io.File
import java.io.IOException
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-09-19 
 */
class JobPostFragment: MVVMBaseFragment<FragmentPostJobBinding, JobPostViewModel>(), View.OnClickListener, OnDialogClickListener,
    PhotoSelectListener, DialogThreeOptionListener, EasyPermissions.PermissionCallbacks, GalleryPickerListener {

    // View model
    private lateinit var jobViewModel: JobPostViewModel
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
    // Last selected type ID
    var lastTypeId = Constant.BLANK
    // Last selected category ID
    var lastCategoryId = Constant.BLANK
    // Job type position
    var typeMutableList = mutableListOf<Job>()
    // Job category position
    var categoryMutableList = mutableListOf<Job>()
    // Progress bar
    private var mProgressbarHandler: ProgressbarHandler? = null
    // Places
    private var mPlaces: PlacesUtils? = null

    override fun getBaseViewModel(): JobPostViewModel {
        jobViewModel = ViewModelProviders.of(this, viewModelFactory)[JobPostViewModel::class.java]
        return jobViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        // Get request job type
        jobViewModel.requestJobTypeData()
        argument?.let {
            mode = JobPostFragmentArgs.fromBundle(it).mode
            postId = JobPostFragmentArgs.fromBundle(it).postId
            userId?.let { userId -> jobViewModel.getDetailPost(userId, postId) }
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_job)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            // Clear current fragment from stack
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_post_job

    override fun initView() {
        activity?.let { context ->
            // Progress bar init
            mProgressbarHandler = ProgressbarHandler(context)
            // Places picker init
            mPlaces = PlacesUtils(context)
            // Init unit price spinner
            jobPostSalaryPriceUnit.applyCustomSpinner(
                context,
                R.array.salaryType,
                R.layout.layout_common_spinner,
                android.R.layout.simple_spinner_dropdown_item)
            // Get job type response
            jobViewModel.jobTypeResponse.observe(viewLifecycleOwner, Observer { response ->
                typeMutableList = response.job.toMutableList()
                val job = ArrayList<String>()
                typeMutableList.forEach { job.add(it.jobName) }
                jobPostType.applyCustomSpinner(
                    context,
                    R.layout.layout_common_spinner,
                    job,
                    android.R.layout.simple_spinner_dropdown_item
                )
                // Get id of item last selected
                jobPostType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {}

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        lastTypeId = typeMutableList[position].jobId
                        DebugLog.e(lastTypeId)
                    }
                }
            })
        }
        // Get detail response observe
        jobViewModel.jobDetailResponse.observe(this, Observer { response ->
            getDetailResponse(response)
        })
        // Get edit response observe
        jobViewModel.jobEditResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Get response observe
        jobViewModel.jobPostResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Handle network status
        jobViewModel.networkState.observe(this, Observer { status ->
            handleResponseNetwork(status)
        })
        // Init adapter horizontal
        mHorizontalAdapter = HorizontalImagesAdapter(::onItemClick)
        // Init images recycler view
        initImageHorizontal()
        // Address picker
        postJobEdAddress.setOnClickListener(this)
        // Pick images
        jobPostSelectImg.setOnClickListener(this)
        // Commit data
        postJobCommitBtn.setOnClickListener(this)
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
            postJobEdNote.setText(response.data.note)
            postJobEdAddress.setText(response.data.address)
            postJobEdContent.setText(response.data.content)
            postJobEdPhone.setText(WindyConvertUtil.getPhoneNumber(response.data.phone))
            val countryCode = WindyConvertUtil.getStringBetween(response.data.phone, "+"," ").toInt()
            jobCountryCodePicker.setCountryForPhoneCode(countryCode)
            postJobEdSalary.setText(response.data.salary)
            postJobTitle.setText(response.data.title)
            val jobTypePosition = typeMutableList.indexOfFirst { it.jobId == response.data.jobTypeId }
            jobPostType.setSelection(jobTypePosition, true)
            postJobEdCategory.setText(response.data.jobCategory)
            jobPostSalaryPriceUnit.setSelection(response.data.salaryType.toInt().minus(1))
            postJobIsTop.isChecked = response.data.isTop == Constant.TOP_CHECKED
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
            R.id.postJobEdAddress -> mPlaces?.displayPlacePicker(this, Constant.REQUEST_CD_PLACE)
            R.id.jobPostSelectImg -> addImages()
            R.id.postJobCommitBtn -> processSendDataPost()
        }
    }

    /**
     * Init images recycler view
     *
     */
    private fun initImageHorizontal() {
        activity?.let { context ->
            // Set up recyclerView
            jobPostRecyclerImg.apply {
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
                    jobViewModel.requestDataPost(
                        userId,
                        JOB_SERVICE_ID,
                        jobViewModel.title,
                        lastTypeId,
                        jobViewModel.category,
                        getTranslateType(),
                        jobViewModel.address,
                        WindyConvertUtil.convertPhoneNumber(jobCountryCodePicker, postJobEdPhone),
                        jobViewModel.salary,
                        jobViewModel.content,
                        jobViewModel.note,
                        if (postJobIsTop.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        mHorizontalAdapter.images
                    )
                }
                Constant.MODE_EDIT -> {
                    jobViewModel.editDataPost(
                        userId,
                        postId,
                        WindyConvertUtil.convertPhoneNumber(jobCountryCodePicker, postJobEdPhone),
                        lastTypeId,
                        jobViewModel.category,
                        getTranslateType(),
                        if (postJobIsTop.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        mHorizontalAdapter.images
                    )
                }
            }
        }
    }

    /**
     * Get salary type by selection
     *
     * @return salary type id
     */
    private fun getTranslateType(): String {
        return when(jobPostSalaryPriceUnit.selectedItemPosition) {
            0, 1 -> jobPostSalaryPriceUnit.selectedItemPosition.plus(1).toString()
            else -> "1"
        }
    }

    /**
     * Display confirm TOP dialog when check to checkbox
     *
     */
    private fun processSendDataPost() {
        // Validate
        val arrayFields = arrayOf<EditText>(postJobTitle, postJobEdAddress, postJobEdPhone, postJobEdSalary, postJobEdContent, postJobEdNote)
        val validate = validateFieldsGlobal(arrayFields, jobCountryCodePicker, postJobEdPhone, null, jobPostParent)
        if (!validate) return
        // Check top then commit post
        if (postJobIsTop.isChecked) {
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
            Snackbar.make(jobPostParent, getString(R.string.limit_photo_message), Snackbar.LENGTH_SHORT).show()
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
        when (requestCode) {
            Constant.REQUEST_CD_PLACE -> mPlaces?.receivedAddressProcess(resultCode, data, postJobEdAddress)
            Constant.REQUEST_GALLERY -> processHandleImage(data)
            Constant.REQUEST_CAMERA -> processHandleCamera()
            else -> DebugLog.e("Not OK")
        }
        // Add images to adapter then display them
        mHorizontalAdapter.addImages(pathArray)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Handle image from camera
     *
     */
    private fun processHandleCamera() {
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

    /**
     * Handle image from gallery or camera
     *
     * @param data
     */
    private fun processHandleImage(data: Intent?) {
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
     * Click on purchase top put top button
     *
     */
    override fun onClickPurchaseButton() {
        // Validate
        val arrayFields = arrayOf<EditText>(postJobTitle, postJobEdAddress, postJobEdPhone, postJobEdSalary, postJobEdContent, postJobEdNote)
        val validate = validateFieldsGlobal(arrayFields, jobCountryCodePicker, postJobEdPhone, null, jobPostParent)
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