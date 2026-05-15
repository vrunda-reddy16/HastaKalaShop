package com.hastakalashop.app.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

data class BarItem(val label: String, val value: Float)

@Composable
fun ShadcnBarChart(
    bars: List<BarItem>,
    barColor: Int,
    modifier: Modifier = Modifier,
    height: Dp = 220.dp
) {
    AndroidView(
        modifier = modifier.fillMaxWidth().height(height),
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setFitBars(true)
                legend.isEnabled = false
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = AndroidColor.parseColor("#E4E4E7")
                axisLeft.textColor = AndroidColor.parseColor("#71717A")
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                xAxis.textColor = AndroidColor.parseColor("#71717A")
                animateY(600)
                setNoDataText("No sales yet")
                setNoDataTextColor(AndroidColor.parseColor("#71717A"))
            }
        },
        update = { chart ->
            val entries = bars.mapIndexed { i, item -> BarEntry(i.toFloat(), item.value) }
            val dataSet = BarDataSet(entries, "").apply {
                color = barColor
                valueTextColor = AndroidColor.parseColor("#09090B")
                valueTextSize = 11f
            }
            chart.data = BarData(dataSet).apply { barWidth = 0.55f }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(bars.map { it.label })
            chart.invalidate()
        }
    )
}