package tech.davidburns.activitytracker.fragments;

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.enter_name_dialog.*
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.interfaces.Dialogable

class MyDialog(@StringRes private val hint: Int) : DialogFragment() {
    private var fragment: Dialogable? = null
    fun setFragment(fragment: Dialogable) {
        this.fragment = fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.enter_name_dialog, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtEnterName?.hint = getString(hint)
        btnEnterName.setOnClickListener {
            val activityName: String = txtEnterName.text.toString()
            if (activityName == "") {
                txtEnterName?.hint = "Cannot be empty!"
            } else {
                val valid: Boolean? = fragment?.dialogString(activityName)
                if (valid!!) {
                    dismiss()
                } else {
                    txtEnterName.setText("")
                    txtEnterName?.hint = "Must be unique!"
                }
            }
        }
    }
}