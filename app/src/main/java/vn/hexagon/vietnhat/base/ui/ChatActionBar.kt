package vn.hexagon.vietnhat.base.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.action_bar_for_chat.view.*
import vn.hexagon.vietnhat.R

/**
 * Created by NhamVD on 2019-10-03.
 */
class ChatActionBar : BaseActionBar {

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  var chatTitleText: String = ""
    get() = titleChatText.text.toString()
    set(value) {
      field = value.trim()
      titleChatText.text = field
    }

  private var chatTitleSize: Int = 0
    set(value) {
      field = value
      setTextSize(titleChatText, field)
    }

  private var chatTitleStyle: Typeface = Typeface.DEFAULT
    set(value) {
      field = value
      setTextStyle(titleChatText, field)
    }

  private var chatTitleColor: Int = 0
    set(value) {
      field = value
      setTextColor(titleChatText, field)
    }

  var avatarUrl: String = ""
    set(value) {
      field = value
      Glide.with(context).load(value)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.drawable.ic_ava_nodata)
        .error(R.drawable.ic_ava_nodata)
        .into(titleAvatarUserImage)
    }

  override fun onInflatedView() {
    actionBarColor = R.color.action_bar_background_color
    chatTitleColor = R.color.color_indigo_26415d
    chatTitleSize = R.dimen.sp_17
    chatTitleStyle = Typeface.DEFAULT_BOLD
    leftButtonResource = R.drawable.ic_back
    rightButtonResource = R.drawable.ic_phone
    leftButtonVisible = true
    rightButtonVisible = true
    onClickLeftButton = { findNavController().popBackStack() }
  }

  override fun getContentLayout(): Int = R.layout.action_bar_for_chat

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