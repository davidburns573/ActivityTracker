package tech.davidburns.activitytracker.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity.view.*
import kotlinx.android.synthetic.main.activity_view.*
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.interfaces.Dialogable

class ActivityViewController : Fragment(), Dialogable {
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
            val dialogFragment =
                MyDialog(R.string.enter_activity_name) //here MyDialog is my custom dialog
            dialogFragment.setFragment(this)
            if (fragmentTransaction != null) {
                dialogFragment.show(fragmentTransaction, "dialog")
            }
        }
    }

    override fun dialogString(str: String) {
        val layoutInflater: LayoutInflater =
            activity?.applicationContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.activity, null)
        view.name.text = str
        val activity = Activity(str)
        activity.initView(view)
        val viewGroup = activityLayout
        viewGroup.addView(view, 0)
    }
}