package com.example.base_clean_architecture.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.base_clean_architecture.R
import com.example.base_clean_architecture.databinding.DialogAlertMessageBinding
import com.example.base_clean_architecture.extension.isTablet

object AlertDialogUtil {

    fun showExceptionNetwork(context: Activity, actionClick: () -> Unit? = {}) {
        showMessage(
            context,
            context.getString(R.string.exception_network),
            cancelable = false,
            onButtonClicked = actionClick
        )
    }

    fun showExceptionServer(context: Activity, actionClick: () -> Unit? = {}) {
        showMessage(
            context,
            context.getString(R.string.error_something_went_wrong),
            cancelable = false,
            onButtonClicked = actionClick
        )
    }

    fun showMessage(
        context: Activity,
        message: String,
        title: String? = null,
        cancelable: Boolean,
        onButtonClicked: () -> Unit? = {}
    ) {
        val inflater = LayoutInflater.from(context)
        val binding = DialogAlertMessageBinding.inflate(inflater)
        val dialog = createAlertDialog(context, cancelable, binding.root)

        binding.txtMessage.text = message
        if (title.isNullOrEmpty())
            binding.txtTitle.visibility = GONE
        else {
            binding.txtTitle.visibility = VISIBLE
            binding.txtTitle.text = title
        }


        dialog.setOnDismissListener {
            onButtonClicked()
        }
        if (!context.isFinishing)
            dialog.show()
    }

    fun dialogMessage(
        context: Context,
        message: String,
        title: String? = null,
        cancelable: Boolean,
        onButtonClicked: () -> Unit
    ): AlertDialog {
        val inflater = LayoutInflater.from(context)
        val binding = DialogAlertMessageBinding.inflate(inflater)
        val dialog = createAlertDialog(context, cancelable, binding.root)

        binding.txtMessage.text = message
        if (title.isNullOrEmpty())
            binding.txtTitle.visibility = GONE
        else {
            binding.txtTitle.visibility = VISIBLE
            binding.txtTitle.text = title
        }

        dialog.setOnDismissListener {
            onButtonClicked()
        }

        return dialog
    }

    fun createAlertDialog(
        context: Context,
        cancelable: Boolean,
        view: View,
        isUseDefaultBackground: Boolean = true,
        isTransparentBackground: Boolean = false
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context).setCancelable(cancelable).setView(view).create()

        dialog.run {
            window?.decorView?.setBackgroundResource(android.R.color.transparent)
            if (isUseDefaultBackground) {
                window?.setBackgroundDrawableResource(R.drawable.border_popup)
            }
            if (isTransparentBackground) {
                window?.setDimAmount(0F)
            }

            // Config size
            val deviceWith: Int = Resources.getSystem().displayMetrics.widthPixels
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            if (context.isTablet()) {
                window?.setLayout((deviceWith * 0.4).toInt(), height)
            } else {
                window?.setLayout((deviceWith * 0.9).toInt(), height)
            }

            // Config window animations
            window?.setWindowAnimations(R.style.DialogAnimationNormal)
        }

        return dialog
    }
}
