package com.example.finalproject.ui.components

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalproject.utils.FoodData
import android.graphics.Color as AndroidColor
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun NutritionBarChart(foodItems: List<FoodData>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.textColor = AndroidColor.GRAY
                legend.textSize = 12f
                setDrawGridBackground(false)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.textColor = AndroidColor.GRAY
                xAxis.setDrawGridLines(false)

                axisLeft.textColor = AndroidColor.GRAY
                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false

                animateY(1000)
            }
        },
        update = { chart ->
            val entries1 = foodItems.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.calories.toFloat())
            }
            val entries2 = foodItems.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.carbs.toFloat())
            }
            val entries3 = foodItems.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.fat.toFloat())
            }
            val entries4 = foodItems.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.protein.toFloat())
            }

            val set1 = BarDataSet(entries1, "Calories").apply {
                color = AndroidColor.parseColor("#4CAF50")
            }
            val set2 = BarDataSet(entries2, "Carbs").apply {
                color = AndroidColor.parseColor("#2196F3")
            }
            val set3 = BarDataSet(entries3, "Fat").apply {
                color = AndroidColor.parseColor("#FF9800")
            }
            val set4 = BarDataSet(entries4, "Protein").apply {
                color = AndroidColor.parseColor("#F44336")
            }

            val groupSpace = 0.4f
            val barSpace = 0.02f
            var barWidth = 0.2f

            val barData = BarData(set1, set2, set3, set4).apply {
                barWidth = barWidth
            }

            chart.data = barData
            chart.groupBars(0f, groupSpace, barSpace)

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(
                foodItems.mapIndexed { index, _ -> "Meal ${index + 1}" }
            )

            chart.invalidate()
        }
    )
}