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

class StatisticsController : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.statistics_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barEntries: ArrayList<BarEntry> = ArrayList()
        barEntries.add(BarEntry(0f, 1f))
        barEntries.add(BarEntry(1f, 2f))
        barEntries.add(BarEntry(2f, 3f))
        barEntries.add(BarEntry(3f, 4f))
        barEntries.add(BarEntry(4f, 2f))
        barEntries.add(BarEntry(5f, 7f))
        barEntries.add(BarEntry(6f, 4f))
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