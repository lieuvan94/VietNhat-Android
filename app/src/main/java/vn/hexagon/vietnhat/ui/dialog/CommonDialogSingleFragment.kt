package vn.hexagon.vietnhat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialog_confirm_common.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment

/*
 * Create by VuNBT on 2019-10-24 
 */
class CommonDialogSingleFragment: BaseDialogFragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dialog_confirm_common, container, false)
        root.dialogCommonContent.text = getString(R.string.thank_for_rating)
        root.dialogCommonConfirmBtn.text = getString(R.string.alert_dialog_confirm_btn)
        root.dialogCommonConfirmBtn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dialogCommonConfirmBtn -> {
                dismiss()
            }
        }
    }
}