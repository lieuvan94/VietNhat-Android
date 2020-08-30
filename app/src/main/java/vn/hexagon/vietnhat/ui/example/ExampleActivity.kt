package vn.hexagon.vietnhat.ui.example

import androidx.lifecycle.ViewModelProviders
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseActivity
import vn.hexagon.vietnhat.base.utils.Consts
import vn.hexagon.vietnhat.base.utils.PreferenceHelper
import vn.hexagon.vietnhat.base.utils.PreferenceHelper.get
import vn.hexagon.vietnhat.base.utils.PreferenceHelper.set
import vn.hexagon.vietnhat.databinding.ActivityExampleUserBinding

/**
 * Created by NhamVD on 2019-08-03.
 */
class ExampleActivity : MVVMBaseActivity<ActivityExampleUserBinding, ExampleActivityViewModel>() {

  private lateinit var exampleActivityViewModel: ExampleActivityViewModel

  override fun getBaseViewModel(): ExampleActivityViewModel {
    exampleActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(ExampleActivityViewModel::class.java)
    return exampleActivityViewModel
  }

  override fun getBindingVariable(): Int = BR.viewmodel

  override fun getLayoutId(): Int = R.layout.activity_example_user

  override fun initView() {
    val prefs = PreferenceHelper.defaultPrefs(this)
    val customPrefs = PreferenceHelper.customPrefs(this,this.packageName)
    prefs[Consts.SharedPrefs.KEY] = 1  //s
    val value :Int? = prefs[Consts.SharedPrefs.KEY]
    val anotherValue: Int? = prefs[Consts.SharedPrefs.KEY, 10] //getter with default value

  }

  override fun initAction() {}
}