package tech.davidburns.activitytracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(private val activities: MutableList<Activity>) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {
    lateinit var title: TextView
    lateinit var secondary: TextView
    lateinit var other: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout (single list item)
        val activityView: View = inflater.inflate(R.layout.activity_card, parent, false)

        // Return a new holder instance
        return ViewHolder(activityView)
    }

    override fun onBindViewHolder(holder: ActivityAdapter.ViewHolder, position: Int) {
        if (activities.size > 0) {
            // Get the data model based on position
            val thisActivity = activities[position]
            title.text = thisActivity.name
        }
    }

    override fun getItemCount(): Int = activities.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            title = itemView.findViewById(R.id.activity_title)
            secondary = itemView.findViewById(R.id.secondary_text)
            other = itemView.findViewById(R.id.other_text)
        }
    }
}