package tech.davidburns.activitytracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_card.view.*
import tech.davidburns.activitytracker.fragments.ActivityViewController
import tech.davidburns.activitytracker.fragments.AddTimerSessionDialog
import tech.davidburns.activitytracker.interfaces.ActivityListener

class ActivityAdapter(
    private val activities: MutableList<Activity>,
    private val onClickListener: OnClickListener,
    private val activityViewController: ActivityViewController
) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>(),
    ActivityListener {
    private lateinit var viewHolder: ViewHolder
    lateinit var title: TextView
    lateinit var secondary: TextView
    lateinit var other: TextView
    lateinit var btnStart: Button
    lateinit var timerView: TextView
    lateinit var activity: Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout (single list item)
        val activityView: View = inflater.inflate(R.layout.activity_card, parent, false)

        viewHolder = ViewHolder(activityView, onClickListener)


        viewHolder.itemView.setOnLongClickListener {
            enterEditMode()
            return@setOnLongClickListener true
        }

        // Return a new holder instance
        return viewHolder
    }

    private fun enterEditMode() {
        viewHolder.enterEditMode()
        activityViewController.enterEditMode()
    }

    fun exitEditMode() {
        viewHolder.exitEditMode()
    }

    override fun onBindViewHolder(holder: ActivityAdapter.ViewHolder, position: Int) {
        if (activities.size > 0) {
            // Get the data model based on position
            activity = activities[position]
            holder.activity = activity

            title.text = activity.name
            activity.sessions.clear()
            activity.sessions.addAll(User.getSessionsFromActivity(activity.name))
            secondary.text =
                "${activity.statistics.totalTimeEver().seconds} seconds"
        }
    }

    override fun getItemCount(): Int = activities.size

    inner class ViewHolder(itemView: View, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var activity: Activity
        private val timer: Timer
        init {
            title = itemView.activity_title
            secondary = itemView.secondary_text
            other = itemView.other_text
            btnStart = itemView.btn_start
            timerView = itemView.timer
            timer = Timer(timerView)
            btnStart.setOnClickListener { btnStartOnClick(); }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
                User.currentActivity = activity
                onClickListener.onClick()
        }

        private fun btnStartOnClick() {
                if (timer.isRunning) {
                    itemView.btn_start.text = User.applicationContext.getString(R.string.stop)
                    timer.pauseTimer()
                } else {
                    val dialog = AddTimerSessionDialog(
                        activities[adapterPosition],
                        timer
                    )
                    activityViewController.addTimerSessionDialog(dialog)
                    btnStart.text = User.applicationContext.getString(R.string.start)
                    timer.runTimer()
                }
        }

        fun enterEditMode() {
            itemView.isEnabled = false
            itemView.btn_start.visibility = View.GONE
        }

        fun exitEditMode() {
            itemView.isEnabled = true
            itemView.btn_start.visibility = View.VISIBLE
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
        var f = from
        var t = to
        if (f > t) {
            f = t.also { t = f } //Swap f and t
        }

        for(index in f..t) {
            User.orderUpdated(index)
        }
    }
}

//private class ActivityObj(val button: Button, val timer: Timer)