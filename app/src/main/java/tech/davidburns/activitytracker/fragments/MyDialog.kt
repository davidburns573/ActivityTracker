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
import kotlinx.android.synthetic.main.add_timer_session_dialog.*
import kotlinx.android.synthetic.main.enter_name_dialog.*
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.Timer
import tech.davidburns.activitytracker.interfaces.Dialogable
import java.time.Duration

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

class AddTimerSessionDialog(val activity: Activity, private val timer: Timer, private val onTimerStopped: () -> Unit) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }

        return inflater.inflate(R.layout.add_timer_session_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_session_title.text = getString(R.string.add_session_title, activity.name)

        length_of_session.text = timer.formattedTime

        btn_yes.setOnClickListener {
            activity.addSession(Duration.ofSeconds(timer.seconds.toLong()))
            timer.stop()
            onTimerStopped()
            dismiss()
        }

        btn_no.setOnClickListener {
            timer.stop()
            dismiss()
        }

        btn_back.setOnClickListener {
            timer.resume()
            dismiss()
        }
    }
}