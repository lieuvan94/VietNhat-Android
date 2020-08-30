package vn.hexagon.vietnhat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialog_three_option.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment

/*
 * Create by VuNBT on 2019-10-16 
 */
class DialogThreeOptionsFragment: BaseDialogFragment() {

    // Callback
    private lateinit var callback: DialogThreeOptionListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            callback = targetFragment as DialogThreeOptionListener
        } catch (e:ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement DialogClickListener interface")
        }
        val root = inflater.inflate(R.layout.fragment_dialog_three_option, container, false)
        root.dialogThreeEditBtn.setOnClickListener {
            callback.onModify(false)
            dismiss() }
        root.dialogThreeDeleteBtn.setOnClickListener {
            callback.onModify(true)
            dismiss() }
        root.dialogThreeCancelBtn.setOnClickListener { dismiss() }
        return root
    }
}