package vn.hexagon.vietnhat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialog_confirm_top.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment
import vn.hexagon.vietnhat.ui.post.OnDialogClickListener

/*
 * Create by VuNBT on 2019-09-18 
 */
class ConfirmTopDialogFragment: BaseDialogFragment(), View.OnClickListener {
    private lateinit var callback: OnDialogClickListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            callback = targetFragment as OnDialogClickListener
        } catch (e:ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement DialogClickListener interface")
        }
        val root = inflater.inflate(R.layout.fragment_dialog_confirm_top, container, false)
        root.closeBtn.setOnClickListener(this)
        root.confirmPurchaseBtn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.closeBtn -> dismiss()
            R.id.confirmPurchaseBtn -> {
                dismiss()
                callback.onClickPurchaseButton()
            }
        }
    }
}