package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.statistics_view.*
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.Session
import tech.davidburns.activitytracker.Statistics
import tech.davidburns.activitytracker.User
import java.time.Duration
import java.time.LocalDateTime
import kotlin.collections.ArrayList

class StatisticsController : Fragment() {
    private lateinit var sessions: List<Session>
    private lateinit var statistics: Statistics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessions = User.getSessionsFromCurrentActivity()
        statistics = Statistics(sessions)
        return inflater.inflate(R.layout.statistics_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val totalTimeEver: Duration = statistics.totalTimeEver()
        total_time.text = stringFromDuration(totalTimeEver)

        val timeLastWeek: Array<Duration> = statistics.timeLastWeek()

        val barEntries: ArrayList<BarEntry> = ArrayList()
        barEntries.add(BarEntry(0f, timeLastWeek[0].toMinutes().toFloat()))
        barEntries.add(BarEntry(1f, timeLastWeek[1].toMinutes().toFloat()))
        barEntries.add(BarEntry(2f, timeLastWeek[2].toMinutes().toFloat()))
        barEntries.add(BarEntry(3f, timeLastWeek[3].toMinutes().toFloat()))
        barEntries.add(BarEntry(4f, timeLastWeek[4].toMinutes().toFloat()))
        barEntries.add(BarEntry(5f, timeLastWeek[5].toMinutes().toFloat()))
        barEntries.add(BarEntry(6f, timeLastWeek[6].toMinutes().toFloat()))
        val barDataSet = BarDataSet(barEntries, "Time")

        var barLabels: ArrayList<String> = formatDateLabels()


        val barData = BarData(barDataSet)
        bar_chart.data = barData
        bar_chart.xAxis.valueFormatter = IndexAxisValueFormatter(barLabels)
        barData.barWidth = .8f
        bar_chart.setFitBars(true)
        bar_chart.setTouchEnabled(true)
        bar_chart.setScaleEnabled(true)
        bar_chart.xAxis.setDrawGridLines(false)
        bar_chart.axisLeft.setDrawGridLines(false)
        bar_chart.axisRight.setDrawGridLines(false)
        bar_chart.description.isEnabled = false
    }

    /**
     * Format a string from a Duration object
     * @param duration
     * @return formatted string
     */
    private fun stringFromDuration(duration: Duration) : String {
        var str = ""
        if (duration.seconds < 60) {
            str += duration.seconds.toString() + " sec"
        } else if (duration.toMinutes() < 60) {
            str += duration.toMinutes().toString() + " min"
            val sec: Long = duration.seconds % 60
            if (sec != 0L) {
                str += " $sec sec"
            }
        } else if (duration.toHours() < 24) {
            str += duration.toHours().toString() + " hrs"
            val min: Long = duration.toMinutes() % 60
            if (min != 0L) {
                str += " $min min"
            }
            val sec: Long = duration.seconds % 60
            if (sec != 0L) {
                str += " $sec sec"
            }
        } else {
            str += duration.toDays().toString() + " days"
            val hrs: Long = duration.toHours() % 24
            if (hrs != 0L) {
                str += " $hrs hrs"
            }
            val min: Long = duration.toMinutes() % 60
            if (min != 0L) {
                str += " $min min"
            }
            val sec: Long = duration.seconds % 60
            if (sec != 0L) {
                str += " $sec sec"
            }
        }
        return str
    }

    /**
     * Format date labels to match current day
     * @return ArrayList of formatted label strings
     */
    private fun formatDateLabels() : ArrayList<String> {
        val barLabels: ArrayList<String> = ArrayList()

        var i = 6
        var currentDay: LocalDateTime
        while(i >= 0) {
            currentDay = LocalDateTime.now().minusDays(i.toLong())
            barLabels.add((currentDay.dayOfMonth).toString())
            i--
        }

        return addSuffix(barLabels)
    }

    /**
     * Add suffixes to array of strings containing numbers
     */
    private fun addSuffix(arrayList: ArrayList<String>) : ArrayList<String> {

        val newArr: ArrayList<String> = ArrayList()

        arrayList.forEach {
            val str: String = when(it) {
                "1", "21", "31" -> "st"
                "2", "22" -> "nd"
                "3", "23" -> "rd"
                else -> "th"
            }
            newArr.add(it + str)
        }

        return newArr
    }
}