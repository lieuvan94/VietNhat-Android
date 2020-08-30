package vn.hexagon.vietnhat.ui.setting.account.password

import android.os.Bundle
import android.view.View
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.action_bar_base.view.*
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_request_sms_cd.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.ui.SimpleActionBar
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.auth.PasswordResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.databinding.FragmentRequestSmsCdBinding
import vn.hexagon.vietnhat.ui.setting.account.AccountInfoViewModel

/*
 * Create by VuNBT on 2019-10-17 
 */
class RequestSmsCodeFragment: MVVMBaseFragment<FragmentRequestSmsCdBinding, AccountInfoViewModel>(),
    View.OnClickListener {

    // View model
    private lateinit var accountInfoViewModel: AccountInfoViewModel
    // Verify code
    private var mVerifyCode = Constant.BLANK
    // Phone
    private var mPhone = Constant.BLANK

    override fun getBaseViewModel(): AccountInfoViewModel {
        accountInfoViewModel = ViewModelProviders.of(this, viewModelFactory)[AccountInfoViewModel::class.java]
        return accountInfoViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        val dataBinding = getBaseViewDataBinding()
        dataBinding.viewmodel = accountInfoViewModel

        // Request verify code
        accountInfoViewModel.requestForgotPassword()
    }

    override fun isShowActionBar(): View? = SimpleActionBar(activity).apply {
        simpleTitleText = getString(R.string.change_pass_lbl)
        leftButtonVisible = true
        leftActionBarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_request_sms_cd

    override fun initView() {
        layoutGetSmsCode.visibility = View.VISIBLE
        // Get phone number
        arguments?.let {
            mPhone = RequestSmsCodeFragmentArgs.fromBundle(it).phone
            accountInfoViewModel.userPhone = mPhone
        }
        // Verify code response
        accountInfoViewModel.verifyCodeResponse.observe(this, Observer { response ->
            getVerifyCodeResponse(response)
        })
        // Get response update account info
        accountInfoViewModel.changePasswordResponse.observe(this, Observer { response ->
            getChangePasswordRequest(response)
        })
        // Bold phone number
        val prefix = getString(R.string.content_sms_code_with_phone_pre)
        val suffix = getString(R.string.content_sms_code_with_phone_suf)
        contentTextWithPhone.text = buildSpannedString { append(prefix).bold { append(mPhone) }.append(suffix) }
        // Handle events onClick
        smsConfirmBtn.setOnClickListener(this)
        smsResendCode.setOnClickListener(this)
        passwordConfirmBtn.setOnClickListener(this)
    }

    /**
     * Handle verify code response
     *
     * @param response
     */
    private fun getVerifyCodeResponse(response: VerifyResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            DebugLog.d("Verify code: ${response.verifyCd}")
            mVerifyCode = response.verifyCd
        } else {
            showAlertDialog(getString(R.string.alert_dialog_title), response?.message, "OK")
        }
    }

    /**
     * Handle events onClick
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.smsConfirmBtn -> {
                processConfirmVerifyCode()
            }
            R.id.smsResendCode -> {
                // Request verify code
                accountInfoViewModel.requestForgotPassword()
            }
            R.id.passwordConfirmBtn ->
                // Request change password
                accountInfoViewModel.sendRequestChangePassword(
                    mPhone, inputNewPasswordEd.text.toString())
        }
    }

    /**
     * Response account info
     *
     * @param response
     */
    private fun getChangePasswordRequest(response: PasswordResponse) {
        if (response.errorId == Constant.RESPOND_CD) {
            DebugLog.d("Change Password Success: ${response.errorId}")
            showAlertDialog(getString(R.string.alert_dialog_notice), getString(R.string.change_pass_success), "OK")
            // Back to previous screen
            findNavController().popBackStack()
        } else {
            showAlertDialog(getString(R.string.alert_dialog_title), response.message, "OK")
        }
    }

    /**
     * Confirm verify code
     *
     */
    private fun processConfirmVerifyCode() {
        // Validate empty
        if (inputSmsCodeEd.text.toString().isEmpty()) {
            inputSmsCodeEd.error = getString(R.string.verify_empty_message)
            inputSmsCodeEd.requestFocus()
            return
        }
        // Validate matching
        if (mVerifyCode == inputSmsCodeEd.text.toString()) {
            layoutGetSmsCode.visibility = View.GONE
            layoutChangePassword.visibility = View.VISIBLE
        } else {
            inputSmsCodeEd.error = getString(R.string.verify_not_match)
            inputSmsCodeEd.requestFocus()
        }
    }

    override fun initAction() {
    }
}