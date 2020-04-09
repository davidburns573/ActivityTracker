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
        total_time.text = totalTimeEver.toMinutes().toString() + " minutes"

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

        val barLabels: ArrayList<String> = ArrayList()
        barLabels.add("Mon")
        barLabels.add("Tue")
        barLabels.add("Wed")
        barLabels.add("Thu")
        barLabels.add("Fri")
        barLabels.add("Sat")
        barLabels.add("Sun")

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
}