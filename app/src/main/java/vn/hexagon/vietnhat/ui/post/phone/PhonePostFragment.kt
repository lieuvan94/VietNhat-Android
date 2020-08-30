package vn.hexagon.vietnhat.ui.post.phone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
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
import kotlinx.android.synthetic.main.fragment_post_phone.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.base.view.PlacesUtils
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.PostDetailResponse
import vn.hexagon.vietnhat.data.model.product.Product
import vn.hexagon.vietnhat.data.model.product.ProductResponse
import vn.hexagon.vietnhat.data.model.translator.PostResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentPostPhoneBinding
import vn.hexagon.vietnhat.ui.dialog.*
import vn.hexagon.vietnhat.ui.gallery.GalleryPickerFragment
import vn.hexagon.vietnhat.ui.gallery.GalleryPickerListener
import vn.hexagon.vietnhat.ui.list.HorizontalImagesAdapter
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import vn.hexagon.vietnhat.ui.post.OnDialogClickListener
import vn.hexagon.vietnhat.ui.post.OnDialogClickWithData
import vn.hexagon.vietnhat.ui.post.ProductPostAdapter
import vn.hexagon.vietnhat.ui.post.restaurant.ProductPostViewModel
import java.io.File
import java.io.IOException
import javax.inject.Inject

/*
 * Create by VuNBT on 2019-10-04 
 */
class PhonePostFragment: MVVMBaseFragment<FragmentPostPhoneBinding, ProductPostViewModel>(), View.OnClickListener,
    OnDialogClickListener, OnDialogClickWithData, PhotoSelectListener, DialogThreeOptionListener,
    EasyPermissions.PermissionCallbacks, GalleryPickerListener {
    // View model
    private lateinit var productPostViewModel: ProductPostViewModel
    // Adapter
    private lateinit var productPostAdapter: ProductPostAdapter
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
    // Mode
    var mode = Constant.MODE_CREATE
    // Post ID
    var postId = Constant.BLANK

    override fun getBaseViewModel(): ProductPostViewModel {
        productPostViewModel =
            ViewModelProviders.of(this, viewModelFactory)[ProductPostViewModel::class.java]
        return productPostViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)
        argument?.let {
            mode = PhonePostFragmentArgs.fromBundle(it).mode
            postId = PhonePostFragmentArgs.fromBundle(it).postId
            userId?.let { userId -> productPostViewModel.getPostInfo(userId, postId) }
            productPostViewModel.getProductList(postId)
        }
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.create_post_phone)
        leftButtonVisible = true
        rightButtonVisible = false
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_post_phone

    override fun initView() {
        activity?.let { context ->
            // Progress bar init
            mProgressbarHandler = ProgressbarHandler(context)
            // Places picker init
            mPlaces = PlacesUtils(context)
        }
        // Get detail response
        productPostViewModel.detailPostResponse.observe(this, Observer { response ->
            getDetailResponse(response)
        })
        // Get response observe
        productPostViewModel.postProductResponse.observe(this, Observer { response ->
            getResponse(response)
        })
        // Get product list response
        productPostViewModel.productPostResponse.observe(this, Observer { response ->
            getProductResponse(response)
        })
        // Handle network status
        productPostViewModel.networkState.observe(this, Observer { status ->
            handleResponseNetwork(status)
        })
        // Handle network status
        productPostViewModel.networkState.observe(this, Observer { status ->
            handleResponseNetwork(status)
        })
        // Init adapter
        productPostAdapter = ProductPostAdapter(ArrayList(), ::onDeleteClick)
        // Init adapter horizontal
        mHorizontalAdapter = HorizontalImagesAdapter(::onItemClick)
        // Set limit size for img upload
        mHorizontalAdapter.specificLimitSize(Constant.MAX_LIMIT_IMG)
        // Horizontal image slide
        initImageHorizontal()
        // Init recyclerView
        initRecycler()
        // Add products
        phoneProductPlaceHoldItem.setOnClickListener(this)
        // Address picker
        phonePostEdAddress.setOnClickListener(this)
        // Commit data
        phonePostCommitBtn.setOnClickListener(this)
        // Select cover image
        phonePostSelectImg.setOnClickListener(this)
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
     * Init images recycler view
     *
     */
    private fun initImageHorizontal() {
        activity?.let { context ->
            // Set up recyclerView
            phonePostRecyclerImg.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = mHorizontalAdapter
            }
        }
    }

    /**
     * Get detail post response
     *
     * @param response
     */
    private fun getDetailResponse(response: PostDetailResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            // Redisplay recycler at edit mode
            initImageHorizontal()
            phonePostEdAddress.setText(response.data.address)
            phonePostEdContent.setText(response.data.content)
            phonePostEdMessenger.setText(response.data.note)
            phonePostEdPhone.setText(WindyConvertUtil.getPhoneNumber(response.data.phone))
            val countryCode = WindyConvertUtil.getStringBetween(response.data.phone, "+"," ").toInt()
            phoneCountryCodePicker.setCountryForPhoneCode(countryCode)
            phonePostEdTitle.setText(response.data.title)
            phonePostPinCheck.isChecked = response.data.isTop == Constant.TOP_CHECKED
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
     * Get product response
     *
     * @param response
     */
    private fun getProductResponse(response: ProductResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            phoneProductLbl.visibility = if (response.data.isNotEmpty()) View.VISIBLE else View.GONE
            // Add products init
            response.data.forEach { productPostAdapter.insertItem(it) }
            // Display all items
            productPostAdapter.isRevealAllItem = true
        } else {
            showAlertDialog("Error", response?.message, "OK")
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
     * Handle events for onClick for views
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.phonePostEdAddress -> mPlaces?.displayPlacePicker(this, Constant.REQUEST_CD_PLACE)
            R.id.phonePostSelectImg -> {
                if (mHorizontalAdapter.itemCount == mHorizontalAdapter.LIMIT_SIZE) {
                    Snackbar.make(phonePostArea, getString(R.string.limit_photo_message), Snackbar.LENGTH_SHORT).show()
                    return
                }
                fragmentManager?.let {
                    val dialog = DialogSelectPhotoFragment()
                    dialog.setTargetFragment(this, 0)
                    dialog.show(it, "SelectPhotoDialog")
                }
            }
            R.id.phoneProductPlaceHoldItem -> {
                fragmentManager?.let {
                    fragmentManager?.let {
                        val dialog = ProductDialogFragment()
                        dialog.setTargetFragment(this, 0)
                        dialog.show(it, "AddProductDialog")
                    }
                }
            }
            R.id.phonePostCommitBtn -> processSendDataPost()
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
                    productPostViewModel.sendDataPost(
                        userId,
                        Constant.PHONE_SERVICE_ID,
                        WindyConvertUtil.convertPhoneNumber(phoneCountryCodePicker, phonePostEdPhone),
                        if (phonePostPinCheck.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        productPostAdapter.list,
                        mHorizontalAdapter.images
                    )
                }
                Constant.MODE_EDIT -> {
                    productPostViewModel.requestEditPost(
                        userId,
                        postId,
                        WindyConvertUtil.convertPhoneNumber(phoneCountryCodePicker, phonePostEdPhone),
                        if (phonePostPinCheck.isChecked) Constant.TOP_CHECKED else Constant.TOP_UNCHECK,
                        productPostAdapter.list,
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
        val arrayFields = arrayOf<EditText>(phonePostEdTitle, phonePostEdAddress, phonePostEdPhone, phonePostEdContent, phonePostEdMessenger)
        val validate = validateFieldsGlobal(arrayFields, phoneCountryCodePicker, phonePostEdPhone, productPostViewModel.img, phonePostArea)
        if (!validate) return
        // Check purchase
        if (phonePostPinCheck.isChecked) {
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
     * Handle send request after click on
     * button confirm top on dialog
     *
     */
    override fun onClickPurchaseButton() {
        // Validate
        val arrayFields = arrayOf<EditText>(phonePostEdTitle, phonePostEdAddress, phonePostEdPhone, phonePostEdContent, phonePostEdMessenger)
        val validate = validateFieldsGlobal(arrayFields, phoneCountryCodePicker, phonePostEdPhone, productPostViewModel.img, phonePostArea)
        if (!validate) return
        // Commit post
        sendDataPost()
    }

    override fun initAction() {
    }

    /**
     * Handle send request add product after
     * close dialog add product
     *
     * @param productName
     * @param productPrice
     * @param productImg
     */
    override fun requestProductData(
        productName: String,
        productPrice: String,
        productImg: String?
    ) {
        val product = Product(productName, productPrice, productImg.toString())
        // Reveal all items
        productPostAdapter.isRevealAllItem = true
        // Display preview product list
        if (productPostAdapter.list.size > 0) {
            // Add products at EDIT MODE
            val list = arrayListOf<Product>()
            list.add(product)
            list.forEach {
                productPostAdapter.insertItem(it)
                productPostAdapter.notifyDataSetChanged() }
        } else {
            // Add products at CREATE MODE
            productPostAdapter.apply {
                insertItem(product)
            }
        }
        phoneProductLbl.visibility = if (productPostAdapter.list.size > 0) View.VISIBLE else View.GONE
    }

    /**
     * Init recycler view
     *
     */
    private fun initRecycler() {
        activity?.let { context ->
            phoneItemRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = productPostAdapter
            }
        }
    }

    /**
     * Handle remove items
     *
     * @param position
     */
    private fun ProductPostAdapter.processRemoveItem(position: Int) {
        val productListUpdate = this.list.removeAt(position)
        this.apply {
            onCurrentListChanged(list, mutableListOf(productListUpdate))
            notifyItemRemoved(position)
            Handler().postDelayed({
                phoneItemRecyclerView.invalidateItemDecorations()
            }, 500)
            phonePostArea.layoutTransition
        }
        Snackbar.make(phonePostArea, getString(R.string.remove_product_message), Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Handle onClick menu delete button
     *
     * @param position
     */
    private fun onDeleteClick(position: Int) {
        productPostAdapter.processRemoveItem(position)
        phoneProductLbl.visibility = if (productPostAdapter.list.size > 0) View.VISIBLE else View.GONE
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
            Constant.REQUEST_CD_PLACE -> mPlaces?.receivedAddressProcess(resultCode, data, phonePostEdAddress)
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
            else -> DebugLog.e("Not OK")
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
            val dialog = GalleryPickerFragment(Constant.MAX_LIMIT_IMG)
            dialog.setTargetFragment(this, 0)
            dialog.show(it, "SelectImgBottomSheet")
        }
    }
}