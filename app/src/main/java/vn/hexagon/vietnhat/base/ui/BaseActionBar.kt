package vn.hexagon.vietnhat.base.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.action_bar_base.view.*
import vn.hexagon.vietnhat.R

/**
 * Created by NhamVD on 2019-08-13.
 */
abstract class BaseActionBar : RelativeLayout {
  constructor(context: Context?)
      : super(context) {
    initView()
  }

  constructor(context: Context?, attrs: AttributeSet?)
      : super(context, attrs) {
    initView()
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr) {
    initView()
  }

  var actionBarColor: Int = 0
    set(value) {
      field = value
      setBackgroundColor(ContextCompat.getColor(context, field))
    }

  var leftButtonVisible: Boolean = true
    set(value) {
      field = value
      leftActionBarButton.visibility = if (field) View.VISIBLE else View.INVISIBLE
    }

  var rightButtonVisible: Boolean = true
    set(value) {
      field = value
      rightActionBarButton.visibility = if (field) View.VISIBLE else View.INVISIBLE
    }

  var leftButtonResource: Int = 0
    set(value) {
      field = value
      leftActionBarButton.setImageResource(value)
    }

  var rightButtonResource: Int = 0
    set(value) {
      field = value
      rightActionBarButton.setImageResource(value)
    }

  var onClickLeftButton: (() -> Unit)? = null

  var onClickRightButton: (() -> Unit)? = null

  private fun initView() {
    View.inflate(context, R.layout.action_bar_base, this)
    LayoutInflater.from(context).inflate(getContentLayout(), contentActionBarLayout, true)
    onInflatedView()
    initActions()
  }


  private fun initActions() {
    leftActionBarButton.setOnClickListener { onClickLeftButton?.invoke() }
    rightActionBarButton.setOnClickListener { onClickRightButton?.invoke() }
  }

  abstract fun onInflatedView()

  abstract fun getContentLayout(): Int
}