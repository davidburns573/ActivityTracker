package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_view.*
import tech.davidburns.activitytracker.ActivityAdapter
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Dialogable


class ActivityViewController : Fragment(), Dialogable, ActivityAdapter.OnClickListener {
    private lateinit var viewAdapter: ActivityAdapter
    private var editMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewAdapter = ActivityAdapter(User.activities, this, this)
        User.addActivityListener(viewAdapter)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
        return inflater.inflate(R.layout.activity_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemTouchHelper.attachToRecyclerView(activity_recycler)
        activity_recycler.adapter = viewAdapter
        activity_recycler.layoutManager =
            LinearLayoutManager(activity)
                .apply {
                    reverseLayout = true
                    stackFromEnd = true
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recyclerview_menu, menu)
        menu.findItem(R.id.delete).apply { isVisible = editMode }
    }

    override fun dialogString(str: String): Boolean {
        User.activities.forEach {
            if (it.name == str) {
                return false
            }
        }
        User.addActivity(str)
        return true
    }

    override fun onClick() {
        val action =
            ActivityViewControllerDirections.actionActivityViewControllerToActivityController()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        User.removeActivityListener(viewAdapter)
        super.onDestroyView()
    }

    fun addTimerSessionDialog(addTimerSessionDialog: AddTimerSessionDialog) {
        addTimerSessionDialog.show(activity?.supportFragmentManager?.beginTransaction()!!, "dialog")
    }

    fun enterEditMode() {
        editMode = true
        activity?.invalidateOptionsMenu()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnAddActivity.visibility = View.GONE
    }

    fun exitEditMode() {
        editMode = false
        activity?.invalidateOptionsMenu()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        viewAdapter.exitEditMode()
        btnAddActivity.visibility = View.VISIBLE
//        for (i in 0 until User.activities.size) {
//            User.orderUpdated(i)
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.delete -> {
            val works = true
            true
        }
        android.R.id.home -> {
            exitEditMode()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0
            ) {
                //    We highlight the ViewHolder here.
                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                //We unhighlight the ViewHolder here.
                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    Log.d("ON_MOVE", "${User.activities[from].name}, from: $from, to: $to")
                    viewAdapter.moveItem(from, to)
                    // Tell adapter to render the model update.
                    viewAdapter.notifyItemMoved(from, to)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) { //Do nothing
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }
}