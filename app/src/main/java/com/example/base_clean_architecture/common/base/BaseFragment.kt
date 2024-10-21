package com.example.base_clean_architecture.common.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.base_clean_architecture.domain.errors.ViewError
import com.example.base_clean_architecture.utils.AlertDialogUtil
import com.example.base_clean_architecture.utils.DialogLoadingUtils
import com.example.base_clean_architecture.utils.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

private typealias FragmentViewBindingInflater<VB> = (
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
) -> VB

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val bindingInflater: FragmentViewBindingInflater<VB>
) : Fragment() {

    protected var binding: VB? = null
    abstract val viewModel: VM
    private var mLoadingDialog: Dialog? = null

    abstract fun initializeViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return bindingInflater.invoke(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        view.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.errorsFlow.collectLatest { viewError ->
                    val errorMessage = when (viewError) {
                        is ViewError.ResourceError -> getString(viewError.resId)
                        is ViewError.StringError -> viewError.error
                    }
                    showError(errorMessage) {
                        handleError(viewError)
                    }
                }
            }

            launch {
                viewModel.progressFlow.collectLatest {
                    Timber.tag("###BaseProject").d("${Thread.currentThread().name} isLoading: $it")
                    showProgress(it)
                }
            }
        }
    }

    open fun showProgress(visible: Boolean) {
        if (visible) showLoadingDialog() else dismissLoadingDialog()
    }

    protected fun showLoadingDialog() {
        try {
            if (mLoadingDialog != null && mLoadingDialog!!.isShowing) return
            if (activity?.isFinishing == false) {
                mLoadingDialog = DialogLoadingUtils.createLoadingDialog(requireContext())
                mLoadingDialog?.setCanceledOnTouchOutside(false)
                mLoadingDialog?.show()
            }
        } catch (e: Exception) {
            return
        }
    }

    protected fun dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog!!.isShowing && activity?.isFinishing == false) {
            mLoadingDialog?.dismiss()
            mLoadingDialog = null
        }
    }

    private fun showError(errorMessage: String?, action: () -> Unit) {
        if (errorMessage == null) return
        AlertDialogUtil.showMessage(
            context = requireActivity(),
            message = errorMessage,
            cancelable = true
        ) {
            action.invoke()
        }
    }

    open fun handleError(viewError: ViewError) = Unit

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        dismissLoadingDialog()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoadingDialog()
    }

    override fun onPause() {
        super.onPause()
        dismissLoadingDialog()
    }
}
