package vn.hexagon.vietnhat.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.Utils
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.constant.Constant.RESPOND_CD
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.databinding.FragmentLoginBinding
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil
import javax.inject.Inject

/**
 * Created by VuNBT on 2019-08-08.
 */
class LoginFragment : MVVMBaseFragment<FragmentLoginBinding, LoginFragmentViewModel>(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        DebugLog.e(p0.toString())
    }

    // View model
    private lateinit var loginViewModel:LoginFragmentViewModel
    // Shared Preference
    @Inject lateinit var sharedPref: SharedPreferences
    // Google Client
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    // Callback Manager
    private lateinit var mCallBackManager:CallbackManager

    override fun getBaseViewModel(): LoginFragmentViewModel {
        loginViewModel = ViewModelProviders
            .of(this, viewModelFactory).get(LoginFragmentViewModel::class.java)
        return loginViewModel
    }

    override fun getBindingVariable(): Int = BR.login

    override fun initData(argument: Bundle?) {
        if (argument != null) {
            val isLogout = LoginFragmentArgs.fromBundle(argument).isLogout
            // Logout from all
            if (isLogout) {
                if (mGoogleSignInClient != null)
                    signOutGooglePlus()
                if (LoginManager.getInstance() != null)
                    signOutFacebook()
            }
        }
    }

    /**
     * Logout from facebook
     */
    private fun signOutFacebook() {
        val token = AccessToken.getCurrentAccessToken()
        if (token != null) {
            LoginManager.getInstance().logOut()
        }
    }
    override fun isShowBottomNavigation(): Boolean = false

    override fun isShowActionBar(): View? = null

    override fun isActionBarOverlap(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun initView() {
        activity?.let {context ->
            // Declared Shared Pref
            val phone = sharedPref.getString(getString(R.string.variable_local_phone), Constant.BLANK)
            val password = sharedPref.getString(getString(R.string.variable_local_password), Constant.BLANK)
            val countryCd = sharedPref.getString(getString(R.string.variable_local_country_cd), Constant.BLANK)
            // Fill countryCd, phone & password if last time login success
            if (countryCd != null && phone != null && password != null
                && countryCd.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                val countryCode = countryCd.replace("+","").toInt()
                countryCodePicker.setCountryForPhoneCode(countryCode)
                edPhone.setText(phone, TextView.BufferType.EDITABLE)
                edPass.setText(password, TextView.BufferType.EDITABLE)
            }
            // Get response from server
            loginViewModel.loginResponse.observe(this, Observer{ respond ->
                getResponse(respond)
            })

            ///////////////////// Setting for G-plus sign-in ////////////////////
            // Declare Google sign-in
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            // Get Client
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

            ///////////////////// Setting for Facebook sign-in ////////////////////
            mCallBackManager = CallbackManager.Factory.create()

            ///////////////////// Handle events onClick //////////////////////////
            // Change to Register form
            registerTitle.setOnClickListener {
                findNavController().navigate(
                    R.id.action_loginFragment_to_registerFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                )
            }
            // Handle event Login button (Facebook)
            loginFacebookBtn.setOnClickListener {
                signInFacebook()
            }
            // Handle event Login button (Phone number)
            loginBtn.setOnClickListener {
                validateInsertField()
            }
            // Handle event Login button (G-plus)
            loginGmailBtn.setOnClickListener {
                signInGooglePlus()
            }
            // Handle event back button (back to previous screen not include register)
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            loginForgotLink.setOnClickListener {
                if(edPhone.text.toString().isEmpty())   {
                     edPhone.apply {
                         error = getString(R.string.error_phone_empty)
                         requestFocus()
                     }
                } else if (!Utils.isValidPhoneNumber(countryCodePicker.selectedCountryCodeWithPlus + edPhone.text.toString())) {
                    edPhone.apply {
                        error = getString(R.string.error_phone_not_correct)
                        requestFocus()
                    }
                } else {
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToRequestSmsCodeFragment(WindyConvertUtil.convertPhoneNumber(countryCodePicker, edPhone))
                    findNavController().navigate(action)
                }
            }
        }
    }

    /**
     * Sign in with facebook account
     */
    private fun signInFacebook() {
        // Read permission
        LoginManager.getInstance().logInWithReadPermissions(this,
            listOf("public_profile", "email"))
        LoginManager.getInstance().loginBehavior = LoginBehavior.DIALOG_ONLY
        // Callback handle login results
        LoginManager.getInstance()
            .registerCallback(mCallBackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    DebugLog.e("Facebook Result: $result")
                    handleRequestFromFacebook(result)
                }

                override fun onCancel() {
                    DebugLog.e("Facebook Result: Sign in cancel...")
                }

                override fun onError(error: FacebookException?) {
                    DebugLog.e("Facebook Result: $error")
                }

            })
    }

    /**
     * Get facebook user info then send to server
     * @param result LoginResult
     */
    private fun handleRequestFromFacebook(result: LoginResult?) {
        val request = GraphRequest.newMeRequest(result?.accessToken) { `object`, _ ->
            try {
                if (`object`.has("id")) {
                    loginViewModel.doFbLogin(`object`.get("email").toString(),
                        `object`.get("id").toString(),
                        `object`.get("name").toString())
                } else {
                    DebugLog.e("Get user FB Info failed: $`object`")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "name,email,id,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

    /**
     * Validate field Phone & Password
     */
    private fun validateInsertField() {
        when {
            edPhone.text.toString().isEmpty() -> edPhone.apply {
                error = getString(R.string.error_phone_empty)
                requestFocus()
            }
            edPass.text.toString().isEmpty() -> edPass.apply {
                error = getString(R.string.error_password_empty)
                requestFocus()
            }
            edPass.text.toString().length < 6 -> edPass.apply {
                error = getString(R.string.error_password_too_short)
                requestFocus()
            }
            !Utils.isValidPhoneNumber(countryCodePicker.selectedCountryCodeWithPlus + edPhone.text.toString()) -> edPhone.apply {
                error = getString(R.string.error_phone_not_correct)
                requestFocus()
            }
            else -> {
                loginViewModel.doLogin(WindyConvertUtil.convertPhoneNumberTrim(countryCodePicker, edPhone),
                    edPass.text.toString()
                )
                // Save platform, which you login with
                saveToLocal(R.string.variable_local_platform, Constant.NORMAL_ACCOUNT)
            }
        }
    }

    /**
     * Sign in G-plus
     */
    private fun signInGooglePlus() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Disconnect account G-plus
     *
     */
    private fun signOutGooglePlus() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener {
            DebugLog.d("Disconnected...")
        }
    }

    /**
     * Save any data to local
     *
     * @param key
     * @param value
     */
    private fun saveToLocal(key:Int, value:String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.apply {
            putString(getString(key), value)
            apply()
        }
    }

    /**
     * Response from server after sign in as phone
     *
     * @param response
     */
    private fun getResponse(response:LoginResponse) {
        DebugLog.e("Login Status: ${response.errorId}")
        if (response.errorId == RESPOND_CD) {
            sharedPref.edit().clear().apply()
            saveUser(countryCodePicker.selectedCountryCodeWithPlus, edPhone.text.toString(), edPass.text.toString())
            // Save userId
            val userId = response.user.userId
            saveToLocal(R.string.variable_local_user_id, userId)
            // Transition to previous screen
            findNavController().popBackStack()
            saveToLocal(R.string.token, Constant.TOKEN)
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    /**
     * Save user information to local
     *
     * @param phone
     * @param password
     */
    private fun saveUser(countryCd: String, phone:String, password:String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.apply {
            putString(getString(R.string.variable_local_country_cd), countryCd)
            putString(getString(R.string.variable_local_phone), phone)
            putString(getString(R.string.variable_local_password), password)
            apply()
        }
    }

    override fun initAction() {
    }

    /**
     * Handle response
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallBackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(result)
        }
    }

    /**
     * Handle result after sign in as G-plus
     *
     * @param result Task<GoogleSignInAccount>
     */
    private fun handleSignInResult(result: Task<GoogleSignInAccount>) {
        if (result.isSuccessful) {
            DebugLog.e("G-plus Success: $result")
            if (activity != null) {
                val account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)!!
                loginViewModel.doGgLogin(account.email.toString(),
                    account.id.toString(),
                    account.displayName.toString())
            }
        } else {
            DebugLog.e("G-plus Failed: $result")
            showAlertDialog("Alert", "Cancel sign in", "OK")
        }
    }
}
