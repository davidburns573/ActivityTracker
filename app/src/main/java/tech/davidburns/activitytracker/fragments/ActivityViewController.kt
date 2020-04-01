package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_view.*
import tech.davidburns.activitytracker.ActivityAdapter
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Dialogable

class ActivityViewController : Fragment(), Dialogable, ActivityAdapter.OnClickListener {
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(activity).apply { reverseLayout = true }
            .apply { stackFromEnd = true }
        viewAdapter = ActivityAdapter(User.database.activities, this)
        return inflater.inflate(R.layout.activity_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity_recycler.adapter = viewAdapter
        activity_recycler.layoutManager = viewManager

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

    override fun dialogString(str: String): Boolean {
        User.database.activities.forEach {
            if (it.name == str) {
                return false
            }
        }
        User.addActivity(str)
        viewAdapter.notifyItemInserted(User.database.activities.size - 1)
        return true
    }

    override fun onClick() {
        val action =
            ActivityViewControllerDirections.actionActivityViewControllerToActivityController()
        findNavController().navigate(action)
    }
}