package tech.davidburns.activitytracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class ActivityAdapter(
    private val activities: MutableList<Activity>,
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {
    lateinit var title: TextView
    lateinit var secondary: TextView
    lateinit var other: TextView
    lateinit var btnStart: Button
    lateinit var timer: TextView
    val timerLabels: ArrayList<TextView> = ArrayList()
    val startButtons: ArrayList<Button> = ArrayList()
    val timers: ArrayList<Timer> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout (single list item)
        val activityView: View = inflater.inflate(R.layout.activity_card, parent, false)

        // Return a new holder instance
        return ViewHolder(activityView, onClickListener)
    }

    override fun onBindViewHolder(holder: ActivityAdapter.ViewHolder, position: Int) {
        if (activities.size > 0) {
            // Get the data model based on position
            val thisActivity = activities[position]
            title.text = thisActivity.name
            thisActivity.setSessionsFromDB()
            secondary.text =
                "${thisActivity.statistics.totalTimeEver().seconds} seconds"
            timerLabels.add(0, timer)
            startButtons.add(0, btnStart)
            timers.add(0, Timer(timer))
        }
    }

    override fun getItemCount(): Int = activities.size

    inner class ViewHolder(itemView: View, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            title = itemView.findViewById(R.id.activity_title)
            secondary = itemView.findViewById(R.id.secondary_text)
            other = itemView.findViewById(R.id.other_text)
            btnStart = itemView.findViewById(R.id.btn_start)
            timer = itemView.findViewById(R.id.timer)

            btnStart.setOnClickListener { btnStartOnClick(); }

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onClickListener.onClick(adapterPosition)
        }

        private fun btnStartOnClick() {
            if (startButtons[adapterPosition].text == "Start") {
                startButtons[adapterPosition].text = "Stop"
                timers[adapterPosition].runTimer()
            } else {
                timerLabels[adapterPosition].text = ""
                startButtons[adapterPosition].text = "Start"
                timers[adapterPosition].stopTimer()
            }
        }

    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
}
