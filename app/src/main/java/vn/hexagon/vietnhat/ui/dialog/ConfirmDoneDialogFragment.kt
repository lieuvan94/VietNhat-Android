package vn.hexagon.vietnhat.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment

/*
 * Create by VuNBT on 2019-09-18 
 */
class ConfirmDoneDialogFragment: BaseDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dialog_done, container, false)
        findNavController().navigate(R.id.homeFragment)
        Handler().postDelayed({
            dismiss()
        }, 2000)
        return root
    }


}