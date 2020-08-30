package vn.hexagon.vietnhat.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.constant.Constant

/*
 * Create by VuNBT on 2019-10-30 
 */
class PlacesUtils(private val context: Context) {
    // Place ID
    private var mPlaceId = Constant.BLANK

    init {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, context.getString(R.string.place_api_key))
        }
    }

    /**
     * Display place picker
     *
     * @param fragment
     */
    fun displayPlacePicker(fragment: Fragment, requestCd: Int) {
        val placeFields = arrayListOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
        // Start the autocomplete intent.
        val intent = context.let {
            Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, placeFields
            )
                .build(it)
        }
        fragment.startActivityForResult(intent, requestCd)
    }

    /**
     * Transition to G-map application with current location
     *
     * @param context
     */
    fun openGmapApplication(context: Context?, name:String) {
        DebugLog.e("https://www.google.com/maps/search/?api=1&query=$name&query_place_id=$mPlaceId")
        val uri = "https://www.google.com/maps/search/?api=1&query=$name"
        val gmmIntentUri: Uri = Uri.parse(uri)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context?.startActivity(mapIntent)
    }

    /**
     * Display data place picker
     *
     * @param resultCd
     * @param data
     * @param targetView
     */
    fun receivedAddressProcess(resultCd: Int, data: Intent?, targetView: EditText) {
        if (data == null) return
        when(resultCd) {
            Activity.RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(data)
                DebugLog.e("${place.name} - ${place.id}")
                mPlaceId = place.id.toString()
                targetView.setText(place.address.toString())
            }
            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data)
                DebugLog.e(status.toString())
            }
            Activity.RESULT_CANCELED -> {
                DebugLog.e("Cancel...")
            }
        }
    }
}