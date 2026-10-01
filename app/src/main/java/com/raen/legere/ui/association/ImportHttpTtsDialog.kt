package com.raen.legere.ui.association

import android.app.Application
import android.os.Bundle
import android.view.View
import com.raen.legere.R
import com.raen.legere.base.BaseDialogFragment
import com.raen.legere.base.BaseViewModel
import com.raen.legere.ui.widget.dialog.CodeDialog

class ImportHttpTtsDialog() : BaseDialogFragment(R.layout.dialog_recycler_view),
    CodeDialog.Callback {

    constructor(source: String, finishOnDismiss: Boolean = false) : this() {
        arguments = Bundle().apply {
            putString("source", source)
            putBoolean("finishOnDismiss", finishOnDismiss)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        dismiss()
    }

    override fun onCodeSave(code: String, requestId: String?) {}
}