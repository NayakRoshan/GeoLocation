package com.roshan.mylocation.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import com.roshan.mylocation.R
import com.roshan.mylocation.callback.DialogNegativePressedListener

class RequestDialog(
    private val listener: DialogNegativePressedListener
) : DialogFragment() {

    private val positiveBtnText : String = "Yes"
    private val negativeBtnText : String = "No"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage(resources.getString(R.string.activate_gps_message))
            .setPositiveButton(positiveBtnText) { _, _ ->
                val serviceActivityIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(serviceActivityIntent)
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