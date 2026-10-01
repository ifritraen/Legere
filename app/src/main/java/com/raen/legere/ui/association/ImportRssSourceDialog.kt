package com.raen.legere.ui.association

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.raen.legere.R
import com.raen.legere.base.BaseDialogFragment
import com.raen.legere.ui.widget.dialog.CodeDialog

class ImportRssSourceDialog() : BaseDialogFragment(R.layout.dialog_recycler_view),
    Toolbar.OnMenuItemClickListener,
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

    override fun onMenuItemClick(item: android.view.MenuItem): Boolean = false
    override fun onCodeSave(code: String, requestId: String?) {}
}