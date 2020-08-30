package vn.hexagon.vietnhat.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_dialog_select_post.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment
import vn.hexagon.vietnhat.base.utils.DebugLog

/**
 * Created by VuNBT on 9/14/2019.
 */
class PostSelectDialogFragment: BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dialog_select_post, container, false)
        root.closeBtn.setOnClickListener { dialog?.dismiss() }
        activity?.let {context ->
            ArrayAdapter.createFromResource(context,
                R.array.listPost,
                android.R.layout.simple_spinner_dropdown_item
                ).also {adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                root.postDialogSpinner.adapter = adapter
                // Handle event for continue button
                root.postDialogContinueBtn.setOnClickListener {
                    handleDestination(root.postDialogSpinner.selectedItemPosition)
                }
            }
        }
        return root
    }

    /**
     * handle destination after click on button continue
     *
     * @param service This is position of service which you selected
     */
    private fun handleDestination(service:Int) {
        dismiss()
        when(service) {
            0 -> findNavController().navigate(R.id.martPostFragment)
            1 -> findNavController().navigate(R.id.jobPostFragment)
            2 -> findNavController().navigate(R.id.transPostFragment)
            3 -> findNavController().navigate(R.id.travelPostFragment)
            4 -> findNavController().navigate(R.id.carPostFragment)
            5 -> findNavController().navigate(R.id.restaurantPostFragment)
            6 -> findNavController().navigate(R.id.spaPostFragment)
            7 -> findNavController().navigate(R.id.phonePostFragment)
            8 -> findNavController().navigate(R.id.visaPostFragment)
            9 -> findNavController().navigate(R.id.deliverPostFragment)
            10 -> findNavController().navigate(R.id.adsPostFragment)
        }
    }
}