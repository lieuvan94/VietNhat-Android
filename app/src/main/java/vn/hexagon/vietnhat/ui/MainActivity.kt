package vn.hexagon.vietnhat.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.activity_main.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseActivity
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.remote.SocketService
import vn.hexagon.vietnhat.databinding.ActivityMainBinding
import javax.inject.Inject

/**
 * Created by NhamVD on 2019-08-17.
 */
class MainActivity : MVVMBaseActivity<ActivityMainBinding, MainViewModel>() {

  private var navController: NavController? = null
  @Inject
  lateinit var mainViewModel: MainViewModel
  @Inject
  lateinit var socketIO: SocketService
  @Inject
  lateinit var sharedPreferences: SharedPreferences
  // UserId
  var userId:String? = Constant.BLANK

  override fun getBaseViewModel(): MainViewModel {
    mainViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(MainViewModel::class.java)
    return mainViewModel
  }

  override fun getBindingVariable(): Int = BR.viewmodel

  override fun getLayoutId(): Int = R.layout.activity_main

  override fun initView() {
    userId = sharedPreferences.getString(getString(R.string.variable_local_user_id), Constant.BLANK)

    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
      val token = instanceIdResult?.token
      if (userId != null && token != null) {
        DebugLog.d(token)
        mainViewModel.requestUpdateToken(userId!!, token)
      }
    }
    navController = findNavController(R.id.navHostFragment)
    navController?.let { navController ->
      mainBottomNavigation.setupWithNavController(navController)
    }
  }

  override fun initAction() {
    navController?.let { navController ->
      mainBottomNavigation.setOnNavigationItemSelectedListener { item ->
        NavigationUI.onNavDestinationSelected(item, navController)
        false
      }
    }
  }

  override fun onResume() {
    super.onResume()
    socketIO.connect()
  }

  override fun onStop() {
    super.onStop()
    socketIO.disconnect()
  }
}