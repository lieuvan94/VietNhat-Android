package vn.hexagon.vietnhat.base.mvvm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_base.view.*
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.di.Injectable
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.Utils
import vn.hexagon.vietnhat.base.utils.permission.PermissionUtil
import vn.hexagon.vietnhat.base.utils.removeFromParent
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-05-21.
 */
abstract class MVVMBaseFragment<T : ViewDataBinding, V : MVVMBaseViewModel> : Fragment(),
  IBase, Injectable {

  interface FragmentCallBack {
    fun onFragmentAttached()

    fun onFragmentDetached(tag: String)
  }

  protected var permissionUtils: PermissionUtil? = null

  protected var baseActionBar: View? = null

  private lateinit var baseViewModel: V

  private lateinit var baseViewDataBinding: T

  protected fun getBaseViewDataBinding(): T = baseViewDataBinding

  abstract fun getBaseViewModel(): V

  abstract fun getBindingVariable(): Int

  abstract fun initData(argument: Bundle?)

  abstract fun isShowActionBar(): View?

  abstract fun isActionBarOverlap(): Boolean

  abstract fun isShowBottomNavigation(): Boolean

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    super.onAttach(context)
    (context as? MVVMBaseActivity<*, *>)?.onFragmentAttached()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(false)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val rootView = inflater.inflate(R.layout.fragment_base, container, false)
    baseViewDataBinding =
      DataBindingUtil.inflate(inflater, getLayoutId(), rootView.contentContainer, true)

    getActionBarView()?.let {
      baseActionBar?.removeFromParent()
      this.baseActionBar = it
      val layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
      rootView.headerContainer.addView(it, layoutParams)
    }

    if (isActionBarOverlap()) {
      val contentContainerLp =
        rootView.contentContainer.layoutParams as? RelativeLayout.LayoutParams
      contentContainerLp?.removeRule(RelativeLayout.BELOW)
    }

    activity?.let { context ->
      if (isShowBottomNavigation()) {
        context.mainBottomNavigation.visibility = View.VISIBLE
      } else {
        context.mainBottomNavigation.visibility = View.GONE
      }
    }
    return rootView

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    baseViewModel = getBaseViewModel()
    baseViewDataBinding.setVariable(getBindingVariable(), baseViewModel)
    baseViewDataBinding.lifecycleOwner = viewLifecycleOwner
    baseViewDataBinding.executePendingBindings()

    //TODO: Maybe use baseActivity
    activity?.let {
      permissionUtils = PermissionUtil(it)
    }

    initView()
    initAction()
    initData(arguments)
  }

  override fun hideKeyBoard() {}

  override fun showLoading(isShow: Boolean) {
    progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
  }

  override fun showAlertDialog(
    title: String?,
    message: String?,
    buttonName: String,
    cb: (() -> Unit)?
  ) {
    context?.let { context ->
      val dialogBuilder = AlertDialog.Builder(context)
        .setCancelable(false)
        .create()
      title?.let {
        dialogBuilder.setTitle(it)
      }

      message?.let {
        dialogBuilder.setMessage(message)
      }

      dialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, buttonName) { dialog, _ ->
        dialog.dismiss()
        cb?.invoke()
      }
      dialogBuilder
    }?.show()
  }

  override fun showAskingDialog(
    title: String?,
    message: String?,
    positionName: String,
    negativeName: String,
    cbPositive: (() -> Unit)?,
    cbNegative: (() -> Unit)?
  ) {
    context?.let { context ->
      val dialogBuilder = AlertDialog.Builder(context)
        .setCancelable(false)
        .create()
      title?.let {
        dialogBuilder.setTitle(it)
      }

      message?.let {
        dialogBuilder.setMessage(message)
      }

      dialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, positionName) { dialog, _ ->
        dialog.dismiss()
        cbPositive?.invoke()
      }

      dialogBuilder.setButton(AlertDialog.BUTTON_NEGATIVE, negativeName) { dialog, _ ->
        dialog.dismiss()
        cbNegative?.invoke()
      }

      dialogBuilder
    }?.show()
  }

  private fun getActionBarView(): View? {
    return if (isShowActionBar() != null) {
      baseActionBar?.removeFromParent() ?: isShowActionBar()
    } else null
  }

  /**
   * Validate all fields
   *
   */
  fun validateFieldsGlobal(arrayFields: Array<EditText>, countryCd: CountryCodePicker?, phoneEditText: EditText?, img:String?, parent: View): Boolean {
    // Validate empty all
    if (!validate(arrayFields)) {
      return false
    }
    // Validate phone number
    if (!Utils.isValidPhoneNumber(countryCd?.selectedCountryCodeWithPlus + phoneEditText?.text.toString()) && phoneEditText != null) {
      phoneEditText.apply {
        error = getString(R.string.error_phone_not_correct)
        requestFocus()
      }
      return false
    }
    /*// Validate image cover
    if (img.isNullOrEmpty()) {
      Snackbar.make(parent, R.string.common_error_img_empty, Snackbar.LENGTH_SHORT).show()
      return false
    }*/
    return true
  }

  private fun validate(fields: Array<EditText>): Boolean {
    for (i in fields.indices) {
      val currentField = fields[i]
      if (currentField.text.toString().isEmpty()) {
        currentField.error = getString(R.string.common_error_ed_empty)
        currentField.requestFocus()
        return false
      }
    }
    return true
  }
}