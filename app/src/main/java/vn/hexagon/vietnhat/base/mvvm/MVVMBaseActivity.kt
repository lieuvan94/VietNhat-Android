package vn.hexagon.vietnhat.base.mvvm

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-05-21.
 */
abstract class MVVMBaseActivity<T : ViewDataBinding, V : MVVMBaseViewModel> : AppCompatActivity(),
  IBase, MVVMBaseFragment.FragmentCallBack, HasSupportFragmentInjector {

  private lateinit var baseViewDataBinding: T

  private lateinit var baseViewModel: V

  protected fun getBaseViewDataBinding(): T = baseViewDataBinding

  abstract fun getBaseViewModel(): V

  abstract fun getBindingVariable(): Int

  var appIsOnBackground = true

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector() = dispatchingAndroidInjector

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    appIsOnBackground = false
    performDataBinding()
    initView()
    initAction()
  }

  override fun hideKeyBoard() {
    val view = this.currentFocus
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
  }

  override fun showAlertDialog(
    title: String?,
    message: String?,
    buttonName: String,
    cb: (() -> Unit)?
  ) {
    val dialogBuilder = AlertDialog.Builder(this)
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

    dialogBuilder.show()
  }

  override fun showAskingDialog(
    title: String?,
    message: String?,
    positionName: String,
    negativeName: String,
    cbPositive: (() -> Unit)?,
    cbNegative: (() -> Unit)?
  ) {
    val dialogBuilder = AlertDialog.Builder(this)
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

    dialogBuilder.show()
  }

  private fun performDataBinding() {
    baseViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
    baseViewModel = getBaseViewModel()
    baseViewDataBinding.setVariable(getBindingVariable(), baseViewModel)
    baseViewDataBinding.executePendingBindings()
  }

  override fun showLoading(isShow: Boolean) {

  }

  override fun onFragmentAttached() {

  }

  override fun onFragmentDetached(tag: String) {

  }
}