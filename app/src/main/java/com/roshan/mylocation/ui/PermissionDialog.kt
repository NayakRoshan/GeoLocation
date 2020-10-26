package com.roshan.mylocation.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import com.roshan.mylocation.R
import com.roshan.mylocation.callback.DialogNegativePressedListener

class PermissionDialog(
    private val listener: DialogNegativePressedListener
) : DialogFragment() {

    private val positiveBtnText : String = "Allow"
    private val negativeBtnText : String = "Deny"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage(resources.getString(R.string.permission_request_message))
            .setPositiveButton(positiveBtnText) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts(
                    "package", context?.packageName,
                    null
                )
                intent.data = uri
                activity?.startActivity(intent)
                dismiss()
            }
            .setNegativeButton(negativeBtnText) { _, _ ->
                dismiss()
                listener.onNegativePressed()
            }.create()

        alertDialog.setCanceledOnTouchOutside(false)

        return alertDialog
    }
}