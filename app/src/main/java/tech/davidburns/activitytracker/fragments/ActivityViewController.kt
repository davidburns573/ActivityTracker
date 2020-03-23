package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_view.*
import tech.davidburns.activitytracker.ActivityAdapter
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Dialogable

class ActivityViewController : Fragment(), Dialogable {
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
<<<<<<< HEAD
        viewManager = LinearLayoutManager(activity).apply { reverseLayout = true }
                                                   .apply { stackFromEnd = true }
        viewAdapter = ActivityAdapter(User.activities)
=======
>>>>>>> Finished ActivityDatabase
        context?.let { User.initDatabase(it) }
        return inflater.inflate(R.layout.activity_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity_recycler.adapter = viewAdapter
        activity_recycler.layoutManager = viewManager

        initializeData()

        User.setActivitiesFromDB()
        for (activity in User.activities) {
            addActivityView(activity.name)
        }

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
        User.activities.forEach {
            if (it.name == str) {
                return false
            }
        }
        User.addActivity(str)
<<<<<<< HEAD
        viewAdapter.notifyItemInserted(User.activities.size - 1)
        return true
    }

    private fun initializeData() {
        val startingIndex = User.activities.size
        User.setActivitiesFromDB()
        viewAdapter.notifyItemRangeInserted(startingIndex, User.activities.size - startingIndex)
=======
        addActivityView(str)
        return true
    }

    private fun addActivityView(name: String) {
        val layoutInflater: LayoutInflater =
            activity?.applicationContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.activity, null)
        view.name.text = name
        val activity = Activity(name)
        activity.initView(view)
        val viewGroup = activityLayout
        viewGroup.addView(view, 0)
>>>>>>> Finished ActivityDatabase
    }
}