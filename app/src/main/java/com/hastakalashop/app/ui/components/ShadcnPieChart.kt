package com.hastakalashop.app.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import android.widget.Toast
import com.github.mikephil.charting.data.Entry

data class PieSlice(val label: String, val value: Float)

@Composable
fun ShadcnPieChart(
    slices: List<PieSlice>,
    colors: List<Int>,
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    centerText: String = ""
) {
    AndroidView(
        modifier = modifier.fillMaxWidth().height(height),
        factory = { ctx ->
            PieChart(ctx).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(AndroidColor.TRANSPARENT)
                holeRadius = 55f
                transparentCircleRadius = 58f
                setUsePercentValues(true)

                // No labels on chart — clean look
                setDrawEntryLabels(false)

                // Legend at bottom — shows color + name
                legend.isEnabled = true
                legend.textSize = 11f
                legend.textColor = AndroidColor.parseColor("#71717A")
                legend.isWordWrapEnabled = true
                legend.xEntrySpace = 10f
                legend.yEntrySpace = 4f

                setExtraOffsets(8f, 8f, 8f, 20f)
                setNoDataText("No sales yet")
                setNoDataTextColor(AndroidColor.parseColor("#71717A"))
                animateY(700)

                // On tap — show product name + % in Toast
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is PieEntry) {
                            val name = e.label ?: ""
                            val pct = String.format("%.1f", e.value)
                            Toast.makeText(
                                ctx,
                                "$name — $pct%",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val entries = slices.map { PieEntry(it.value, it.label) }
            val dataSet = PieDataSet(entries, "").apply {
                setColors(colors.toIntArray(), 255)
                sliceSpace = 2f
                selectionShift = 8f  // slice pops out when tapped
                valueTextSize = 12f
                valueTextColor = AndroidColor.WHITE
                valueFormatter = PercentFormatter(chart)
            }
            chart.data = PieData(dataSet)
            chart.centerText = centerText
            chart.setCenterTextSize(14f)
            chart.setCenterTextColor(AndroidColor.parseColor("#09090B"))
            chart.invalidate()
        }
    )
} 