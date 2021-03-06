package tech.davidburns.activitytracker.fragments

import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_view.*
import tech.davidburns.activitytracker.ActivityAdapter
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Dialogable
import kotlin.properties.Delegates

class ActivityViewController : Fragment(), Dialogable, ActivityAdapter.OnClickListener {
    private lateinit var viewAdapter: ActivityAdapter
    private var editMode: Boolean = false

    var selectMode by Delegates.observable(false) { _, oldValue, newValue ->
        if (newValue != oldValue) {
            activity?.invalidateOptionsMenu()
        }
    }

    fun createNewAdapter() {
        viewAdapter = ActivityAdapter(User.activities, this, this)
        User.addActivityListener(viewAdapter)
        activity_recycler.adapter = viewAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewAdapter = ActivityAdapter(User.activities, this, this)
        User.addActivityListener(viewAdapter)
        setHasOptionsMenu(true)
        activity?.undo_all?.setOnClickListener { undoRecyclerviewChanges() }
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
        fab.setOnClickListener {
            if (editMode) {
                exitEditMode()
            } else {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recyclerview_menu, menu)
        menu.findItem(R.id.delete_selected).apply { isVisible = selectMode }
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
        activity?.undo_all?.setOnClickListener(null)
        super.onDestroyView()
    }

    fun addTimerSessionDialog(addTimerSessionDialog: AddTimerSessionDialog) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.let { addTimerSessionDialog.show(it, "dialog") }
    }

    fun enterEditMode() {
        editMode = true
        activity?.invalidateOptionsMenu()
        activity?.undo_all?.visibility = View.VISIBLE
        fab.setImageIcon(
            Icon.createWithResource(
                User.applicationContext,
                R.drawable.ic_check_black_24dp
            )
        )
        fab.contentDescription = getString(R.string.commit_changes)
        fab.tooltipText = getString(R.string.commit_changes)
    }

    fun exitEditMode() {
        exit()
        viewAdapter.exitEditMode()
    }

    private fun abortEditMode() {
        exit()
    }

    private fun exit() {
        editMode = false
        selectMode = false
        activity?.undo_all?.visibility = View.GONE
        fab.setImageIcon(
            Icon.createWithResource(
                User.applicationContext,
                R.drawable.ic_add_black_24dp
            )
        )
        fab.contentDescription = getString(R.string.add_activity)
        fab.tooltipText = getString(R.string.add_activity)
    }

    fun updateNumberSelected(numSelected: Int) {
        val selectedViewHolderCounter = activity?.number_selected
        if (numSelected > 0) {
            selectedViewHolderCounter?.apply {
                visibility = View.VISIBLE
                text = numSelected.toString()
            }
        } else {
            selectedViewHolderCounter?.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.delete_selected -> {
            viewAdapter.deleteSelected()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun undoRecyclerviewChanges() {
        viewAdapter.undoChanges()
        abortEditMode()
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    //Adapted from: https://medium.com/@yfujiki/drag-and-reorder-recyclerview-items-in-a-user-friendly-manner-1282335141e9
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
                    if (!editMode) {
                        viewAdapter.enterEditMode()
                    }
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