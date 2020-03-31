package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_view.*
import tech.davidburns.activitytracker.ActivityAdapter
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Dialogable
import tech.davidburns.activitytracker.util.FirestoreDatabase
import tech.davidburns.activitytracker.util.NativeDatabase

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
        viewAdapter = ActivityAdapter(User.activities, this)
        return inflater.inflate(R.layout.activity_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity_recycler.adapter = viewAdapter
        activity_recycler.layoutManager = viewManager

        viewAdapter.notifyItemRangeInserted(0, User.activities.size)

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
        viewAdapter.notifyItemInserted(User.activities.size - 1)
        return true
    }

    override fun onClick(position: Int) {
        val action = ActivityViewControllerDirections.
                     actionActivityViewControllerToActivityController(position)
        findNavController().navigate(action)
    }
}