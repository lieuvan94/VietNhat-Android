package vn.hexagon.vietnhat.ui.gallery

import android.app.Dialog
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_gallery_picker.view.*
import vn.hexagon.vietnhat.R


/*
 * Create by VuNBT on 2020-01-06 
 */
class GalleryPickerFragment(private val limit: Int): BottomSheetDialogFragment(), View.OnClickListener {

    // Adapter gallery
    private lateinit var galleryAdapter: GalleryAdapter
    // Images selected
    private var imagesSelected = arrayListOf<String>()
    // Call back
    private lateinit var callback: GalleryPickerListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.apply {
            setCanceledOnTouchOutside(true)
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            callback = targetFragment as GalleryPickerListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement DialogClickListener interface")
        }
        val root =
            LayoutInflater.from(context).inflate(R.layout.fragment_gallery_picker, container, false)
        activity?.let { context ->
            galleryAdapter = GalleryAdapter(context, ::onSelectedItem)
            root.galleryPickerRv.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 3)
                adapter = galleryAdapter
            }
            // Disable touch outside dialog
            isCancelable = false
            // Insert images list
            galleryAdapter.insertList(getAllImagesList())
            galleryAdapter.notifyDataSetChanged()
        }
        // Grant events onClick
        root.galleryPickerCloseBtn.setOnClickListener(this)
        root.galleryPickerSendBtn.setOnClickListener(this)
        return root
    }

    /**
     * Get images list from device
     *
     * @return imageList
     */
    private fun getAllImagesList(): ArrayList<String> {
        val imagesList = ArrayList<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val cursor = activity?.contentResolver?.query(
            uri, projection, null,
            null, "$orderBy DESC"
        )
        cursor?.let {
            var absolutePath: String?
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val columnIndexFolderName =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                absolutePath = cursor.getString(columnIndexData)
                imagesList.add(absolutePath)
            }
        }

        cursor?.close()
        Log.d("LIST IMAGE", imagesList.size.toString())
        return imagesList
    }

    private fun onSelectedItem(uri: String, isSelected: Boolean) {
        // Add/remove uri following checkbox state
        if (!isSelected) {
            imagesSelected.remove(uri)
        } else {
            imagesSelected.add(uri)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.galleryPickerCloseBtn -> {
                if (galleryAdapter.mSparseBooleanArray.size() > 0) {
                    displayAskingDialog()
                } else {
                    dialog?.dismiss()
                }
            }
            R.id.galleryPickerSendBtn -> {
                if (imagesSelected.size in 1..limit) {
                    callback.onSendImages(imagesSelected)
                    galleryAdapter.apply {
                        mSparseBooleanArray.clear()
                        notifyDataSetChanged()
                    }
                    dialog?.dismiss()
                } else if (imagesSelected.size > limit) {
                    displayAlertLimitDialog()
                }
            }
        }
    }

    /**
     * Display asking dialog to confirm uncheck image or not
     *
     */
    private fun displayAskingDialog() {
        activity?.let { context ->
            val builder = AlertDialog.Builder(context)
            builder
                .setTitle(getString(R.string.dialog_confirm_close))
                .setMessage(getString(R.string.dialog_confirm_close_msg))
                .setPositiveButton(R.string.dialog_confirm_close_cancel) { dialog, which -> dialog.dismiss() }
                .setNegativeButton(R.string.dialog_confirm_close_back) { dialog, which ->
                    galleryAdapter.apply {
                        mSparseBooleanArray.clear()
                        notifyDataSetChanged()
                    }
                    dialog.dismiss()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }
    }

    /**
     * Display alert dialog about limit photos
     *
     */
    private fun displayAlertLimitDialog() {
        activity?.let { context ->
            val builder = AlertDialog.Builder(context)
            builder
                .setMessage(getString(R.string.limit_photo_message))
                .setPositiveButton(R.string.confirm_ok) { dialog, which -> dialog.dismiss() }
            val alert: AlertDialog = builder.create()
            alert.show()
        }
    }
}