package tech.davidburns.activitytracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
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
        val barDataSet: BarDataSet = createBarDataSet(timeLastWeek)

        val barLabels: ArrayList<String> = formatDateLabels()


        val barData = BarData(barDataSet)
        barData.setValueTextSize(11f)
        barData.setValueTextColor(Color.DKGRAY)
        bar_chart.data = barData
        bar_chart.xAxis.valueFormatter = IndexAxisValueFormatter(barLabels)
        bar_chart.xAxis.textColor = Color.DKGRAY
        bar_chart.xAxis.textSize = 11f
        bar_chart.xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        bar_chart.xAxis.setDrawAxisLine(false)

        bar_chart.axisLeft.isEnabled = false
        bar_chart.axisRight.isEnabled = false

        bar_chart.setDrawGridBackground(false)
        bar_chart.setDrawValueAboveBar(false)
        bar_chart.setDrawBorders(false)
        barData.barWidth = .8f
        bar_chart.setFitBars(true)
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

    /**
     * format weekly data to be placed in bar graph
     */
    private fun createBarDataSet(arr: Array<Duration>) : BarDataSet {

        val newArr: ArrayList<Float> = ArrayList()
        val max: Long = arr.max()!!.seconds
        val type: String

        when {
            max < 60 -> {
                arr.forEach {
                    newArr.add(it.seconds.toFloat())
                }
                type = "Seconds"
            }
            max < 3600 -> {
                arr.forEach {
                    val num: Float = (it.seconds / 60.0).toFloat()
                    newArr.add(num)
                }
                type = "Minutes"
            }
            max < 86400 -> {
                arr.forEach {
                    val num: Float = (it.seconds / 3600.0).toFloat()
                    newArr.add(num)
                }
                type = "Hours"
            }
            else -> {
                arr.forEach {
                    val num: Float = (it.seconds / 86400.0).toFloat()
                    newArr.add(num)
                }
                type = "Days"
            }
        }

        val barEntries: ArrayList<BarEntry> = ArrayList()
        var counter = 0f;
        newArr.forEach {
            barEntries.add(BarEntry(counter, it))
            counter++
        }

        return BarDataSet(barEntries, type)
    }
}