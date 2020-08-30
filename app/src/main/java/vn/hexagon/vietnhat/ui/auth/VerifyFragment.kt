package vn.hexagon.vietnhat.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_verify_phone.*
import vn.hexagon.vietnhat.BR
import vn.hexagon.vietnhat.R
import vn.hexagon.vietnhat.base.mvvm.MVVMBaseFragment
import vn.hexagon.vietnhat.base.utils.DebugLog
import vn.hexagon.vietnhat.base.view.ProgressbarHandler
import vn.hexagon.vietnhat.constant.Constant
import vn.hexagon.vietnhat.data.model.auth.LoginResponse
import vn.hexagon.vietnhat.data.model.auth.VerifyResponse
import vn.hexagon.vietnhat.data.remote.NetworkState
import vn.hexagon.vietnhat.databinding.FragmentVerifyPhoneBinding

/*
 * Create by VuNBT on 2019-10-30 
 */
class VerifyFragment: MVVMBaseFragment<FragmentVerifyPhoneBinding, RegisterViewModel>(),
    View.OnClickListener {

    // View model
    private lateinit var registerViewModel: RegisterViewModel
    // Progress bar
    private lateinit var mProgressBar: ProgressbarHandler
    // Verify code
    private var mVerifyCd = Constant.BLANK

    override fun getBaseViewModel(): RegisterViewModel {
        registerViewModel = ViewModelProviders.of(this, viewModelFactory)[RegisterViewModel::class.java]
        return registerViewModel
    }

    override fun getBindingVariable(): Int = BR.viewmodel

    override fun initData(argument: Bundle?) {
        mVerifyCd = arguments?.getString("verifyCd").toString()
        DebugLog.d("Verify code: $mVerifyCd")
    }

    override fun isShowActionBar(): View? = null

    override fun isActionBarOverlap(): Boolean = false

    override fun isShowBottomNavigation(): Boolean = false

    override fun getLayoutId(): Int = R.layout.fragment_verify_phone

    override fun initView() {
        // Init progress bar
        activity?.let { context ->
            mProgressBar = ProgressbarHandler(context)
        }
        // Register response
        registerViewModel.getResponse().observe(this, Observer { response ->
            getRegisterResponse(response)
        })
        // Resend code response
        registerViewModel.verifyCodeResponse.observe(this, Observer { response ->
            getResendCodeResponse(response)
        })
        // Network response
        registerViewModel.networkState.observe(this, Observer { response ->
            getNetworkResponse(response)
        })
        // Grant clickable for button resend
        verifyResendBtn.setOnClickListener(this)
        // Grant clickable for button back
        verifyBackBtn.setOnClickListener(this)
        // Grant clickable for button verify
        verifyConfirmBtn.setOnClickListener(this)
    }

    /**
     * Handle network response with progress bar
     *
     * @param response
     */
    private fun getNetworkResponse(response: NetworkState?) {
        when(response) {
            NetworkState.LOADING -> mProgressBar.show()
            NetworkState.LOADED -> mProgressBar.hide()
            NetworkState.ERROR -> mProgressBar.hide()
        }
    }


    /**
     * Ger resend code response
     *
     * @param response
     */
    private fun getResendCodeResponse(response: VerifyResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            mVerifyCd = response.verifyCd
            DebugLog.d("Verify code resend: $mVerifyCd")
            showAlertDialog(getString(R.string.alert_dialog_notice),
                getString(R.string.resend_code_message), "OK")
        } else {
            showAlertDialog(getString(R.string.alert_dialog_notice),
                response?.message, "OK")
        }
    }

    /**
     * Get register response
     *
     * @param response
     */
    private fun getRegisterResponse(response: LoginResponse?) {
        if (response?.errorId == Constant.RESPOND_CD) {
            showAlertDialog(getString(R.string.alert_dialog_notice), response.message, "OK")
            findNavController().navigate(
                R.id.action_verifyFragment_to_loginFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.verifyFragment, true).build()
            )
        } else {
            showAlertDialog(getString(R.string.alert_dialog_title), response?.message, "OK")
        }
    }

    /**
     * Handle onClick events
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.verifyResendBtn -> resendCodeProcess()
            R.id.verifyBackBtn -> {
                findNavController().navigate(
                    R.id.action_verifyFragment_to_registerFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.verifyFragment, true).build()
                )
            }
            R.id.verifyConfirmBtn -> verifyCodeRegister()
        }
    }

    /**
     * Verify code of phone number
     *
     */
    private fun verifyCodeRegister() {
        // Check not insert code
        if (verifyEdInputCode.text.toString().isEmpty()) {
            verifyEdInputCode.error = getString(R.string.verify_empty_message)
            verifyEdInputCode.requestFocus()
            return
        }
        // Check verify code not match
        if (mVerifyCd == verifyEdInputCode.text.toString()) {
            registerViewModel.doSignUp(arguments?.getString("accountName").toString(),
                arguments?.getString("accountPhone").toString(),
                arguments?.getString("accountPassword").toString())
        } else {
            verifyEdInputCode.error = getString(R.string.verify_not_match)
            verifyEdInputCode.requestFocus()
        }
    }

    /**
     * Resend code to verify phone number
     *
     */
    private fun resendCodeProcess() {
        val phone = arguments?.getString("accountPhone").toString()
        registerViewModel.getVerifyCode(phone)
    }

    override fun initAction() {
    }
}