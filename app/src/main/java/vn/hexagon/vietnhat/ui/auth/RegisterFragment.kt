package vn.hexagon.vietnhat.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_register.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.utils.Utils
import vn.hexagon.vietnhat.constant.Constant.RESPOND_CD
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.databinding.FragmentRegisterBinding
import vn.hexagon.vietnhat.ui.list.WindyConvertUtil

class RegisterFragment : MVVMBaseFragment<FragmentRegisterBinding, RegisterViewModel>() {
    // View model
    private lateinit var registerViewModel: RegisterViewModel

    override fun getBaseViewModel(): RegisterViewModel {
        registerViewModel = ViewModelProviders.of(this, viewModelFactory)[RegisterViewModel::class.java]
        return registerViewModel
    }

    override fun getBindingVariable(): Int = BR.register

    override fun initData(argument: Bundle?) {
    }

    override fun isShowBottomNavigation(): Boolean = false

    override fun isShowActionBar(): View? = null

    override fun isActionBarOverlap(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_register

    override fun initView() {
        loginTitle.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.registerFragment, true).build()
            )
        }
        // Response register account
        registerViewModel.getResponse().observe(this, Observer { respond ->
            getResponse(respond)
        })
        // Response verify code
        registerViewModel.verifyCodeResponse.observe(this, Observer { response ->
            getResponseVerify(response)
        })
        // Register button set onClick
        registerBtn.setOnClickListener {
            validateInsertField()
        }
        // Back button set onClick
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Get response verify API
     *
     * @param response
     */
    private fun getResponseVerify(response: VerifyResponse?) {
        if (response?.errorId == RESPOND_CD) {
            DebugLog.i("Request verify code success")
            // Send data to bundle
            val bundle = Bundle()
            bundle.apply {
                putString("verifyCd", response.verifyCd)
                putString("accountName", registerEdName.text.toString())
                putString(
                    "accountPhone",
                    WindyConvertUtil.convertPhoneNumber(countryCodePickerRegister, registerEdPhone)
                )
                putString("accountPassword", registerEdPassword.text.toString())
            }
            // Transit to verify screen
            findNavController().navigate(
                R.id.action_registerFragment_to_verifyFragment,
                bundle,
                NavOptions.Builder().setPopUpTo(R.id.registerFragment, true).build()
            )
        } else {
            showAlertDialog("Error", response?.message, "OK")
        }
    }


    /**
     * Validate insert data at edit texts
     *
     */
    private fun validateInsertField() {
        when {
            registerEdName.text.isEmpty() -> registerEdName.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            registerEdPhone.text.isEmpty() -> registerEdPhone.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            !Utils.isValidPhoneNumber(countryCodePickerRegister?.selectedCountryCodeWithPlus
                    + registerEdPhone.text.toString()) -> registerEdPhone.apply {
                error = getString(R.string.error_phone_not_correct)
                requestFocus()
            }
            registerEdPassword.text.isEmpty() -> registerEdPassword.apply {
                error = getString(R.string.common_error_ed_empty)
                requestFocus()
            }
            registerEdPassword.text.isNotEmpty()
                    && registerEdPassword.text.length < 6 -> registerEdPassword.apply {
                error = getString(R.string.error_password_too_short)
                requestFocus()
            }
            !TextUtils.equals(
                registerEdPassword.text.toString(),
                registerEdRepassword.text.toString()
            ) -> {
                registerEdPassword.apply {
                    error = getString(R.string.error_password_not_match)
                    requestFocus()
                }
                registerEdRepassword.apply {
                    error = getString(R.string.error_password_not_match)
                    requestFocus()
                }
            }
            else -> {
                // Request verify code
                registerViewModel.getVerifyCode(WindyConvertUtil.convertPhoneNumber(countryCodePickerRegister, registerEdPhone))
            }
        }
    }

    /**
     * Get response login result from server
     *
     * @param response
     */
    private fun getResponse(response: LoginResponse) {
        DebugLog.e("Register Status: ${response.errorId}")
        if (response.errorId == RESPOND_CD) {
            showAlertDialog(response.message, "OK")
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.registerFragment, true).build()
            )
        } else {
            showAlertDialog("Error", response.message, "OK")
        }
    }

    override fun initAction() {
    }
}