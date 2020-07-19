package tech.davidburns.activitytracker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_card.view.*
import tech.davidburns.activitytracker.fragments.ActivityViewController
import tech.davidburns.activitytracker.fragments.AddTimerSessionDialog
import tech.davidburns.activitytracker.interfaces.ActivityListener
import tech.davidburns.activitytracker.util.ActivityListDiff
import java.util.*
import kotlin.properties.Delegates

class ActivityAdapter(
    private val activities: MutableList<Activity>,
    private val onClickListener: OnClickListener,
    private val activityViewController: ActivityViewController
) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>(),
    ActivityListener {
    private lateinit var activityListDiff: ActivityListDiff
    private val editModeListeners = ArrayList<(Boolean) -> Unit>()
    private val selectedActivities: MutableList<ViewHolder> = mutableListOf()
    private var activitiesBackup: MutableList<Activity> = mutableListOf()

    private val defaultColor by lazy {
        ResourcesCompat.getColor(
            User.applicationContext.resources,
            R.color.offWhite,
            null
        )
    }

    private val selectedColor by lazy {
        ResourcesCompat.getColor(
            User.applicationContext.resources,
            R.color.colorAccent,
            null
        )
    }

    private var editMode by Delegates.observable(false) { _, oldValue, newValue ->
        editModeListeners.forEach {
            it(newValue)
        }
        if (!oldValue && newValue) {
            backup()
        }
    }

    //Anonymous class stored as a value
    private val longClick =
        View.OnLongClickListener {
            enterEditMode()
            true
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout (single list item)
        val activityView: View = inflater.inflate(R.layout.activity_card, parent, false)

        val viewHolder = ViewHolder(activityView, onClickListener)

        viewHolder.itemView.setOnLongClickListener(longClick)
        viewHolder.itemView.btn_start.setOnLongClickListener(longClick) //Allows entry to edit mode if start button is long pressed

        // Return a new holder instance
        return viewHolder
    }

    fun enterEditMode() {
        activityListDiff = ActivityListDiff() //Create new ActivityListDiff
        editMode = true
        activityViewController.enterEditMode()
    }

    fun exitEditMode() {
        editMode = false
        clearCounter()
        activityListDiff.commitToDatabase() //Commit ListDiff changes to database
    }

    override fun onBindViewHolder(holder: ActivityAdapter.ViewHolder, position: Int) {
        if (activities.size > 0) {
            // Get the data model based on position
            holder.activity = activities[position]

            //Settle sessions TODO this should not happen in onBindViewHolder
            holder.activity.sessions.clear()
            holder.activity.sessions.addAll(User.getSessionsFromActivity(holder.activity.name))

            //Set texts
            holder.title.text = holder.activity.name
            holder.secondary.text = User.applicationContext.getString(
                R.string.seconds_text,
                holder.activity.statistics.totalTimeEver().seconds.toString()
            )

            //Setup timer
            TimerManager.mapOfTimers[holder.activity]?.addListener {
                holder.updateTime(it)
            }

            //Setup on click listeners
            holder.itemView.btn_start.setOnClickListener { holder.btnStartOnClick() }
            holder.itemView.btn_delete.setOnClickListener { holder.deleteActivity() }
            holder.itemView.setOnClickListener(holder)

            if (User.intentActivity == holder.activity.name) {
                TimerManager.mapOfTimers[holder.activity]?.let { holder.btnStopOnClick(it) }
                User.intentActivity = null
            }
        }
    }

    override fun getItemCount(): Int = activities.size

    inner class ViewHolder(itemView: View, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var activity: Activity
        val title: TextView = itemView.activity_title
        val secondary: TextView = itemView.secondary_text
        val other = itemView.other_text

        init {
            editModeListeners.add(::updateEditMode)
        }

        override fun onClick(v: View?) {
            if (editMode) {
                if (itemView.isActivated) {
                    selectedActivities.remove(this)
                } else {
                    selectedActivities.add(this)
                }
                itemView.isActivated = !itemView.isActivated
                itemView.activity_card.setCardBackgroundColor(if (itemView.isActivated) selectedColor else defaultColor)
                activityViewController.updateNumberSelected(selectedActivities.size)
                activityViewController.apply {
                    val size = selectedActivities.size
                    updateNumberSelected(size)
                    selectMode = size > 0
                }
            } else {
                User.currentActivity = activity
                onClickListener.onClick()
            }
        }

        internal fun btnStartOnClick() {
            TimerManager.initializeTimer(activity, title.text.toString()) { timer ->
                timer.addListener(::updateTime)
                itemView.btn_start.text = User.applicationContext.getString(R.string.stop)
                itemView.btn_start.setOnClickListener {
                    btnStopOnClick(timer)
                }
            }
        }

        internal fun btnStopOnClick(timer: Timer) {
            timer.pause()
            val dialog = AddTimerSessionDialog(
                activities[adapterPosition],
                timer,
                ::onTimerStopped
            )
            activityViewController.addTimerSessionDialog(dialog)
        }

        private fun onTimerStopped() {
            TimerManager.mapOfTimers[activity]?.removeListener(::updateTime)
            itemView.btn_start.text = User.applicationContext.getString(R.string.start)
            itemView.btn_start.setOnClickListener { btnStartOnClick() }
            itemView.timer.text = ""
        }

        internal fun deleteActivity() {
            val index = adapterPosition
            val deletedActivity = User.deleteActivityAt(index)
            activityListDiff.itemDeleted(deletedActivity, index)
            if (activities.size == 0) {
                activityViewController.exitEditMode()
            }
        }

        private fun updateEditMode(editMode: Boolean) {
            if (editMode) {
                enterEditMode()
            } else {
                exitEditMode()
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun enterEditMode() {
            itemView.btn_start.visibility = View.GONE
            itemView.btn_delete.visibility = View.VISIBLE
            itemView.isLongClickable = false
            itemView.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activityViewController.startDragging(this)
                }
                return@setOnTouchListener false //Stop view from being highlighted
            }
        }

        private fun exitEditMode() {
            itemView.btn_start.visibility = View.VISIBLE
            itemView.btn_delete.visibility = View.GONE
            itemView.isLongClickable = true
            itemView.setOnTouchListener(null) //Clear onTouchListener (Ignore touch)
            itemView.isActivated = false
            itemView.activity_card.setCardBackgroundColor(defaultColor)
        }

        internal fun updateTime(formattedTime: String) {
            User.mainActivity.runOnUiThread {
                itemView.timer.text = formattedTime
            }
        }

        fun clearTimer() {
            itemView.timer.text = ""
            itemView.btn_start.apply {
                text = User.applicationContext.getText(R.string.start)
                setOnClickListener { btnStartOnClick() }
            }
        }
    }

    interface OnClickListener {
        fun onClick()
    }

    override fun itemChanged(index: Int) {
        notifyItemChanged(index)
    }

    override fun itemRemoved(index: Int) {
        notifyItemRemoved(index)
    }

    override fun itemAdded(index: Int) {
        notifyItemInserted(index)
    }

    override fun itemRangeAdded(start: Int, itemCount: Int) {
        notifyItemRangeInserted(start, itemCount)
    }

    fun moveItem(from: Int, to: Int) {
        val removed = activities.removeAt(from)
        activities.add(to, removed)
        activityListDiff.itemMoved(from, to)

        if (from > to) {
            for (index in (to + 1)..from) {
                activityListDiff.itemMoved(index - 1, index)
            }
        } else {
            for (index in from until to) {
                activityListDiff.itemMoved(index + 1, index)
            }
        }
    }

    fun deleteSelected() {
        for (viewHolder in selectedActivities) {
            viewHolder.deleteActivity()
        }
        clearCounter()
        activityViewController.selectMode = false
    }

    private fun clearCounter() {
        selectedActivities.clear()
        activityViewController.updateNumberSelected(0) //Disable counter
    }

    fun undoChanges() {
        activities.clear()
        activitiesBackup.forEach {
            activities.add(it)
        }
        editMode = false
        clearCounter()
        activityViewController.createNewAdapter()
    }

    private fun backup() {
        activitiesBackup.clear()
        activitiesBackup.addAll(activities.shallowCopy())
    }
}

private fun MutableList<Activity>.shallowCopy(): Collection<Activity> {
    val tempList: MutableList<Activity> = mutableListOf()
    forEach {
        tempList.add(it)
    }
    return tempList
}

//private class ActivityObj(val button: Button, val timer: Timer)