package vn.hexagon.vietnhat.ui.dialog.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import kotlinx.android.synthetic.main.fragment_dialog_ads.*
import kotlinx.android.synthetic.main.fragment_dialog_ads.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil


/*
 * Create by VuNBT on 2019-10-10 
 */
class CommonSearchDialogFragment(private val serviceName: String): BaseDialogFragment(), View.OnClickListener {

    // Listener
    private lateinit var callback: DialogSearchClickListener
    // Keyword field
    private lateinit var titleField: EditText
    // Address field
    private lateinit var predictionAddress: TextView
    // Close button
    private lateinit var closeBtn: ImageView
    // Seek button
    private lateinit var seekButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            callback = targetFragment as DialogSearchClickListener
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement DialogSearchClickListener interface")
        }
        // Root view
        val root = inflater.inflate(R.layout.fragment_dialog_ads, container, false)
        // Dialog title
        root.dialogTitleLbl.text = serviceName
        // Title field
        titleField = root.findViewById(R.id.inputAds)
        // Address field
        predictionAddress = root.findViewById(R.id.inputAddress)
        // Close button
        closeBtn = root.findViewById(R.id.adsCloseBtn)
        // Seek button
        seekButton = root.findViewById(R.id.adsSeekBtn)
        // Handle events onClick
        predictionAddress.setOnClickListener(this)
        closeBtn.setOnClickListener(this)
        seekButton.setOnClickListener(this)

        return root
    }
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.inputAddress -> {
                displayPlacePicker()
            }
            R.id.adsCloseBtn -> dismiss()
            R.id.adsSeekBtn -> {
                callback.searchTriggered(titleField.text.toString(), predictionAddress.text.toString(), null)
                dismiss()
            }
        }
    }

    /**
     * Receive data from intent
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.REQUEST_CD_PLACE && data != null) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    DebugLog.e("${place.name} - ${place.id}")
                    predictionAddress.text = place.address.toString()
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data)
                    DebugLog.e(status.toString())
                }
                Activity.RESULT_CANCELED -> {
                    DebugLog.e("Cancel...")
                    predictionAddress.text = Constant.BLANK
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}