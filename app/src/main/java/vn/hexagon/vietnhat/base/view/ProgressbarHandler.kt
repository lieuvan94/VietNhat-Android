package vn.hexagon.vietnhat.base.view

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import vn.hexagon.vietnhat.R


/**
 * Created by VuNBT on 10/24/2019.
 */
class ProgressbarHandler(context: Context) {
    private var mProgressBar: ProgressBar
    private var mParentLayout: RelativeLayout

    init {
        val layout =
            (context as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup
        mProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        mProgressBar.isIndeterminate = true
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        mParentLayout = RelativeLayout(context)
        mParentLayout.gravity = Gravity.CENTER
        mParentLayout.background = ContextCompat.getDrawable(context, R.color.color_bg_dialog_outside)
        mParentLayout.addView(mProgressBar)
        layout.addView(mParentLayout, params)
        hide()
    }

    fun show() {
        mParentLayout.visibility = View.VISIBLE
    }

    fun hide() {
        mParentLayout.visibility = View.GONE
    }
}