package vn.hexagon.vietnhat.ui.dialog

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_dialog_add_item_product.view.*
import pub.devrel.easypermissions.EasyPermissions
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment
import vn.hexagon.vietnhat.base.utils.*
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.BLANK
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import vn.hexagon.vietnhat.ui.post.OnDialogClickWithData
import java.io.File
import java.io.IOException

/**
 * Created by VuNBT on 9/22/2019.
 */
class ProductDialogFragment: BaseDialogFragment(), View.OnClickListener, PhotoSelectListener, EasyPermissions.PermissionCallbacks {

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // Do nothing
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when(requestCode) {
            Constant.REQUEST_CD_GRANT_PERMISSION -> {
                this.openGallery(false, Constant.REQUEST_CD_PERMISSION)
            }
            Constant.REQUEST_CD_GRANT_PERMISSION_CAMERA -> {
                activity?.let { context -> this.processTakePhoto(context) }
            }
        }
    }

    override fun onSelectPhoto(isCamera: Boolean) {
        if (isCamera) {
            insertImages(Constant.REQUEST_CAMERA)
        } else {
            insertImages(Constant.REQUEST_GALLERY)
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

    private lateinit var previewImg:ImageView
    private lateinit var previewImgName:TextView
    private lateinit var productNameEd:EditText
    private lateinit var productPriceEd:EditText
    private lateinit var previewImgArea: LinearLayout
    private lateinit var parentDialog: RelativeLayout
    private lateinit var callback: OnDialogClickWithData
    private var imgDecorableString:String? = BLANK
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            callback = targetFragment as OnDialogClickWithData
        } catch (e:ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement DialogClickListener interface")
        }
        val root = inflater.inflate(R.layout.fragment_dialog_add_item_product, container, false)
        parentDialog = root.findViewById(R.id.productDialogParent)
        previewImg = root.findViewById(R.id.spaAddDialogPreviewImg)
        previewImgName = root.findViewById(R.id.spaAddDialogPreviewName)
        productNameEd = root.findViewById(R.id.spaAddDialogEdName)
        productPriceEd = root.findViewById(R.id.spaAddDialogEdPrice)
        previewImgArea = root.findViewById(R.id.spaPreviewImgArea)
        //:TODO - add unit for product
//        productUnit = root.spaAddDialogUnit.selectedItem.toString()
        root.spaAddDialogCloseBtn.setOnClickListener(this)
        root.spaAddDialogImgSelector.setOnClickListener(this)
        root.spaAddDialogConfirmBtn.setOnClickListener(this)
        return root
    }

    /**
     * Validate fields input
     *
     * @return true if fields is empty, false is other
     */
    private fun EditText.validateField(): Boolean {
        return if (this.text.toString().isEmpty()) {
            this.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            true
        } else {
            false
        }
    }

    /**
     * Handle onClick views
     *
     * @param view
     */
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.spaAddDialogCloseBtn -> dismiss()
            R.id.spaAddDialogImgSelector -> {
                fragmentManager?.let {
                    val dialog = DialogSelectPhotoFragment()
                    dialog.setTargetFragment(this, 0)
                    dialog.show(it, "SelectPhotoPlatform")
                }
            }
            R.id.spaAddDialogConfirmBtn -> {
                // Validate fields
                if (productNameEd.validateField() || productPriceEd.validateField()) {
                    return
                }
                // Validate image
                if (imgDecorableString.toString().isEmpty()) {
                    Snackbar.make(parentDialog, getString(R.string.common_error_img_empty), Snackbar.LENGTH_SHORT).show()
                    return
                }
                // Request add product to list
                callback.requestProductData(productNameEd.text.toString(), productPriceEd.text.toString(), imgDecorableString)
                // dismiss dialog
                dismissSafety()
            }
        }
    }

    /**
     * Handle data received from intent
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
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
                        imgDecorableString = columnIndex?.let { cursor.getString(it) }
                        // Close cursor
                        cursor?.close()
                        // Add value image path
                        // Set and visible preview upload image
                        DebugLog.e("Image name adding: $imgDecorableString")
                        previewImgArea.visibility = View.VISIBLE
                        previewImg.apply {
                            setImageBitmap(BitmapFactory.decodeFile(imgDecorableString))
                            clipToOutline = true
                        }
                        val file = File(imgDecorableString)
                        previewImgName.text = file.name.toString()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Constant.REQUEST_CAMERA -> {
                previewImgArea.visibility = View.VISIBLE
                val imageFile = File(currentPhotoPath)

                // Display image
                previewImg.apply {
                    setImageURI(Uri.fromFile(imageFile))
                    clipToOutline = true
                }
                // Display image name
                previewImgName.text = imageFile.name.toString()
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
                imgDecorableString = imageFile.path
            }
            else -> DebugLog.e("Not OK")
        }
    }
}