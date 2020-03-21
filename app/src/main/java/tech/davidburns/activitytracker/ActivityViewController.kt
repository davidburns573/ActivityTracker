package tech.davidburns.activitytracker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity.view.*
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.enter_name_dialog.*


class ActivityViewController : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddActivity.setOnClickListener {
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val prev = activity?.supportFragmentManager?.findFragmentByTag("dialog")
            if (prev != null) {
                fragmentTransaction?.remove(prev)
            }
            fragmentTransaction?.addToBackStack(null)
            val dialogFragment = MyDialog() //here MyDialog is my custom dialog
            dialogFragment.setActivityViewController(this)
            if (fragmentTransaction != null) {
                dialogFragment.show(fragmentTransaction, "dialog")
            }
        }
    }

    fun addActivity(name: String) {
        val layoutInflater: LayoutInflater = activity?.applicationContext?.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.activity, null)
        view.name.text = name
        val activity = Activity(name)
        activity.initView(view)
        val viewGroup = activityLayout
        viewGroup.addView(view, 0)
    }
}

class MyDialog : DialogFragment() {

    private var fragment: ActivityViewController? = null
    fun setActivityViewController(actViewCont: ActivityViewController) {
        fragment = actViewCont
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
        btnEnterName.setOnClickListener {
            val activityName: String = txtEnterName.text.toString()
            if (activityName == "") {
                txtEnterName?.hint = "Cannot be empty!";
            } else {
                dismiss()
                fragment?.addActivity(activityName)
            }
        }
    }
}