package vn.hexagon.vietnhat.base.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.ui.dialog.search.DialogSearchClickListener

/**
 *
//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//                    Pray for no Bugs
 * =====================================================
 * Name：VuNBT
 * Create on：2019-08-21
 * =====================================================
 */
open class BaseDialogFragment : DialogFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.window?.apply {
      clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      setBackgroundDrawableResource(android.R.color.transparent)
      // Init places
      context?.let { context ->
        if (!Places.isInitialized()) {
          Places.initialize(context.applicationContext, getString(R.string.place_api_key))
        }
      }
    }
    return dialog
  }

  /***
   * Display place picker
   */
  fun displayPlacePicker() {
    val placeFields = arrayListOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
    // Start the autocomplete intent.
    val intent = context?.let {
      Autocomplete.IntentBuilder(
        AutocompleteActivityMode.FULLSCREEN, placeFields
      )
        .build(it)
    }
    startActivityForResult(intent, Constant.REQUEST_CD_PLACE)
  }

  /***
   * Display place picker
   */
  fun displayPlacesPicker(requestCd: Int) {
    val placeFields = arrayListOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
    // Start the autocomplete intent.
    val intent = context?.let {
      Autocomplete.IntentBuilder(
        AutocompleteActivityMode.FULLSCREEN, placeFields
      )
        .build(it)
    }
    startActivityForResult(intent, requestCd)
  }

  override fun show(
    manager: FragmentManager,
    tag: String?
  ) {
    super.show(manager, tag)
  }
}