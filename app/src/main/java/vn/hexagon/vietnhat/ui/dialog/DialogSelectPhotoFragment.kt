package vn.hexagon.vietnhat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialog_photo_select.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment

/*
 * Create by VuNBT on 2019-10-16 
 */
class DialogSelectPhotoFragment: BaseDialogFragment() {

    // Callback
    private lateinit var callback: PhotoSelectListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            callback = targetFragment as PhotoSelectListener
        } catch (e:ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement DialogClickListener interface")
        }
        val root = inflater.inflate(R.layout.fragment_dialog_photo_select, container, false)
        root.dialogPhotoCameraBtn.setOnClickListener {
            callback.onSelectPhoto(true)
            dismiss() }
        root.dialogPhotoGalleryBtn.setOnClickListener {
            callback.onSelectPhoto(false)
            dismiss() }
        root.dialogPhotoCancelBtn.setOnClickListener { dismiss() }
        return root
    }
}