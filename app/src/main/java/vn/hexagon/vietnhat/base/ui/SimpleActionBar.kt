package vn.hexagon.vietnhat.base.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.action_bar_simple_text.view.*
import vn.hexagon.vietnhat.R

/**
 * Created by NhamVD on 2019-08-14.
 */
class SimpleActionBar : BaseActionBar {

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  var simpleTitleText: String = ""
    get() = titleSingleText.text.toString()
    set(value) {
      field = value.trim()
      titleSingleText.text = field
    }

  var simpleTitleSize: Int = 0
    set(value) {
      field = value
      setTextSize(titleSingleText, field)
    }

  var simpleTitleStyle: Typeface = Typeface.DEFAULT
    set(value) {
      field = value
      setTextStyle(titleSingleText, field)
    }

  var simpleTitleColor: Int = 0
    set(value) {
      field = value
      setTextColor(titleSingleText, field)
    }

  override fun onInflatedView() {
    // Init default
    actionBarColor = R.color.action_bar_background_color
    simpleTitleColor = R.color.color_indigo_26415d
    simpleTitleSize = R.dimen.sp_17
    simpleTitleStyle = Typeface.DEFAULT_BOLD
    leftButtonResource = R.drawable.ic_back
  }

  override fun getContentLayout(): Int = R.layout.action_bar_simple_text

  /**
   * Set text color
   * @param color: the color resource id
   */
  private fun setTextColor(view: TextView, color: Int) {
    view.setTextColor(ContextCompat.getColor(context, color))
  }

  /**
   * Set text style
   */
  private fun setTextStyle(view: TextView, style: Typeface) {
    view.typeface = style
  }

  /**
   * Set text size
   * @param size: The dimen resource id
   */
  private fun setTextSize(view: TextView, size: Int) {
    view.textSize = resources.getDimension(size) / resources.displayMetrics.scaledDensity
  }
}