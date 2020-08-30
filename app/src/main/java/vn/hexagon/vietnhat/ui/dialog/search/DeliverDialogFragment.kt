package vn.hexagon.vietnhat.ui.dialog.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.ui.BaseDialogFragment

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
 * Create on：2019-09-25
 * =====================================================
 */
class DeliverDialogFragment : BaseDialogFragment(), View.OnClickListener {

    // Listener
    private lateinit var callback: DialogSearchDeliverListener
    // Keyword field
    private lateinit var titleField: EditText
    // Road type selector
    private lateinit var roadType: Spinner
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
            callback = targetFragment as DialogSearchDeliverListener
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement DialogSearchClickListener interface")
        }
        val root = inflater.inflate(R.layout.fragment_dialog_deliver, container, false)
        // Title field
        titleField = root.findViewById(R.id.inputDeliver)
        // Road type
        roadType = root.findViewById(R.id.deliverWaySelector)
        // Close button
        closeBtn = root.findViewById(R.id.deliverCloseBtn)
        // Seek button
        seekButton = root.findViewById(R.id.deliverSeekBtn)
        // Handle events onClick
        closeBtn.setOnClickListener(this)
        seekButton.setOnClickListener(this)
        return root
    }

    /**
     * Handle event onClick
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.deliverCloseBtn -> dismiss()
            R.id.deliverSeekBtn -> {
                callback.requestDeliverSearch(
                    titleField.text.toString(),
                    getRoadTypeId(roadType.selectedItemPosition)
                )
                dismiss()
            }
        }
    }

    /**
     * Get road type id
     *
     * @param position
     * @return
     */
    private fun getRoadTypeId(position:Int): String? {
        return when(position) {
            0 -> "1"
            1 -> "2"
            else -> "1"
        }
    }
}